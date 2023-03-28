package com.example.slide.ui.select_image

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.slide.MyApplication
import com.example.slide.R
import com.example.slide.ads.AdLoadingDialog
import com.example.slide.ads.InterstitialHelperV2
import com.example.slide.ads.OnInterstitialCallback
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.example.slide.database.entities.Draft
import com.example.slide.event.ImageLoadStateChangedEvent
import com.example.slide.event.ImageSelectedChangedEvent
import com.example.slide.local.PreferencesHelper
import com.example.slide.model.Album
import com.example.slide.model.Image
import com.example.slide.repository.DraftRepository
import com.example.slide.ui.create.SaveDraftDialog
import com.example.slide.ui.edit_image.EditImageActivity
import com.example.slide.ui.home.MainActivity
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.util.*
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SelectActivity : BaseActivity(), SaveDraftDialog.OnSaveDraftListener {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val uiScope = CoroutineScope(Dispatchers.Main)

    private var adLoadDialog: AdLoadingDialog? = null

    private lateinit var draftRepository: DraftRepository

    private lateinit var draft: Draft

    override fun initViewTools() = InitViewTools({
        R.layout.activity_select
    })

    companion object {

        const val EXTRA_START_MODE = "mode"

        const val EXTRA_FIRST_TIME = "first"

        const val STATE_LOADING = 0

        const val STATE_LOADED = 1

        const val MODE_START = 0

        const val MODE_ADD_IMAGE = 1

        const val MODE_SWAP = 2

        private const val REQUEST_EDIT_IMAGE = 15

        const val EXTRA_EDITED_IMAGE_URL = "new_url"

        const val EXTRA_IMAGES = "extra_images"

        const val EXTRA_DRAFT = "extra_draft"

        const val EXTRA_IMAGE_LIST = "image_list"

        fun getIntent(url: String): Intent {
            val intent = Intent()
            intent.putExtra(EXTRA_EDITED_IMAGE_URL, url)
            return intent
        }

        fun getIntent(context: Context, mode: Int): Intent {
            val intent = Intent(context, SelectActivity::class.java)
            intent.putExtra(EXTRA_START_MODE, mode)
            return intent
        }

        fun getIntent(images: ArrayList<Image>, draft: Draft, mode: Int, context: Context): Intent {
            val intent = Intent(context, SelectActivity::class.java)
            intent.putExtra(EXTRA_START_MODE, mode)
            intent.putExtra(EXTRA_IMAGES, images)
            intent.putExtra(EXTRA_DRAFT, draft)
            return intent
        }

    }

    private val selectFragment by lazy {
        SelectFragment.getInstance()
    }

    private val preferencesHelper by lazy {
        PreferencesHelper(this)
    }

    private var unitId = 0f

    private var isVip = false

    var selectedImages = ArrayList<Image>()

    var mode = MODE_START

    private var imagePosition = -1

    val albums = ArrayList<Album>()

    var state = STATE_LOADING

    private var imageListSize = 0

    private var isFirstTime = true

    override fun extractData(bundle: Bundle) {
        super.extractData(bundle)
        mode = bundle.getInt(EXTRA_START_MODE)
        isFirstTime = bundle.getBoolean(EXTRA_FIRST_TIME, true)
        bundle.getParcelable<Draft>(EXTRA_DRAFT)?.let { draft = it }
        bundle.getSerializable(EXTRA_IMAGES)?.let {
            selectedImages = it as ArrayList<Image>
        }
        if (mode != MODE_START) {
            imageListSize = selectedImages.size
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_START_MODE, mode)
        outState.putBoolean(EXTRA_FIRST_TIME, isFirstTime)
        outState.putSerializable(EXTRA_IMAGES, selectedImages)
        outState.putParcelable(EXTRA_DRAFT, draft)
        outState.putSerializable(EXTRA_IMAGE_LIST, selectedImages)
    }

    override fun initConfiguration(savedInstanceState: Bundle?) {
        super.initConfiguration(savedInstanceState)
        draftRepository = DraftRepository(this)
        isVip = PreferencesHelper(this).isVip()
        unitId = preferencesHelper.getKeyImageId()
        if (savedInstanceState != null) return
        if (mode != MODE_SWAP) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.root_frame, selectFragment, SelectFragment::class.java.name)
                .commit()
        } else {
            if (getFragmentByTag(SwapsFragment.TAG) == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.root_frame, SwapsFragment.newInstance(), SwapsFragment.TAG)
                    .addToBackStack(SwapsFragment.TAG)
                    .commit()
            }
        }
    }

    override fun initTask() {
        super.initTask()
        if (PermissionUtils.isHavePermission(this))
            scanImageFolders().start()
        else
            PermissionUtils.requestPermission(this)
        initDraft()
    }

    private fun initDraft() {
        if (this::draft.isInitialized) {
            return
        }

        draft = Draft()
        draft.title = SimpleDateFormat("MMM dd, hh.mm a", Locale.US).format(Date())

        CoroutineScope(Dispatchers.IO).launch {
            val insertedId = draftRepository.saveAsDraft(draft)
            draft = draftRepository.getDraft(insertedId)[0]
            Timber.d("InsertedId: $insertedId")
            FileUtils.getImagesTempDir(this@SelectActivity, insertedId)
        }
    }

    override fun onStart() {
        isVip = PreferencesHelper(this).isVip()
        super.onStart()
    }

    private fun openActivity(intent: Intent) {
        InterstitialHelperV2.showInterstitialAd(this, object : OnInterstitialCallback {
            override fun onWork() {
                startActivity(intent)
            }
        })
    }

    private fun openActivityForResult(intent: Intent, code: Int) {
        InterstitialHelperV2.showInterstitialAd(this, object : OnInterstitialCallback {
            override fun onWork() {
                startActivityForResult(intent, code)
            }
        })
    }

    fun getCurrentMode(): Int {
        return mode
    }

    fun isDataChanged(): Boolean {
        return selectedImages.size != imageListSize
    }

    fun doneSelectImages() {
        lifecycleScope.launchWhenResumed { saveDraft() }
        if (getCurrentMode() == MODE_ADD_IMAGE) {
            if (!isDataChanged() && selectedImages.size >= 2) {
                finish()
                return
            }
            if (selectedImages.size < 2) {
                Toast.makeText(
                    this,
                    getText(R.string.select_more_than_1_images_for_create_video),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                setResult(
                    Activity.RESULT_OK,
                    VideoCreateActivity.getIntent(this, selectedImages)
                )
                finish()
            }
        } else {
            if (selectedImages.size < 2) {
                Toast.makeText(
                    this,
                    getText(R.string.select_more_than_1_images_for_create_video),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (isFirstTime) {
                    isFirstTime = false
                    try {
                        FileUtils.removePreviewImageTempDir(this)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
                if (getFragmentByTag(SwapsFragment.TAG) == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.root_frame,
                            SwapsFragment.newInstance(),
                            SwapsFragment.TAG
                        )
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    private fun getFragmentByTag(tag: String): Fragment? {
        return supportFragmentManager.findFragmentByTag(tag)
    }

    fun doneSwapImages(isImageEdit: Boolean) {
        lifecycleScope.launchWhenResumed { saveDraft() }
        if (getCurrentMode() == MODE_SWAP) {
            if (!isImageEdit) {
                finish()
                return
            }
            setResult(Activity.RESULT_OK, VideoCreateActivity.getIntent(this, selectedImages))
            finish()
        } else {
            if (selectedImages.size < 2) {
                Toast.makeText(
                    this,
                    getText(R.string.select_more_than_1_images_for_create_video),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                openActivity(
                    VideoCreateActivity.getIntent(this, draft)
                )
            }
        }
    }

    override fun onBackPressed() {
        if (mode == MODE_START) {
            when {
                supportFragmentManager.backStackEntryCount > 0 -> {
                    supportFragmentManager.popBackStack()
                }
                selectedImages.size < 2 -> {
                    finish()
                }
                else -> {
                    SaveDraftDialog.createInstance()
                        .show(supportFragmentManager, SaveDraftDialog.TAG)
                }
            }
        } else {
            finish()
        }
    }

    fun setSelectedImages(image: Image, position: Int) {
        Log.d("kimkaka", "name:" + image.name)
        unitId += 1
        preferencesHelper.saveKeyImageId(unitId)
        val newImage = Image(
            image.id,
            image.albumId,
            image.albumName,
            image.url,
            image.name,
            image.countNumber,
            unitId
        )
        if (VersionUtil.isAndroid6OrLess()) {
            if (selectedImages.size < 70) {
                image.increaseSelectCount()
                selectedImages.add(newImage)
                EventBus.getDefault().post(ImageSelectedChangedEvent(position))
            } else {
                FirebaseAnalytics.getInstance(this).logEvent("over_70", null)
                Toast.makeText(this, getString(R.string.msg_limited_images, 70), Toast.LENGTH_SHORT)
                    .show()
            }
        } else if (VersionUtil.isAndroid7OrLess()) {
            if (selectedImages.size < 140) {
                image.increaseSelectCount()
                selectedImages.add(newImage)
                EventBus.getDefault().post(ImageSelectedChangedEvent(position))
            } else {
                FirebaseAnalytics.getInstance(this).logEvent("over_140", null)
                Toast.makeText(
                    this,
                    getString(R.string.msg_limited_images, 140),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } else if (VersionUtil.isAndroid8OrLess()) {
            if (selectedImages.size < 200) {
                image.increaseSelectCount()
                selectedImages.add(newImage)
                EventBus.getDefault().post(ImageSelectedChangedEvent(position))
            } else {
                FirebaseAnalytics.getInstance(this).logEvent("over_200", null)
                Toast.makeText(
                    this,
                    getString(R.string.msg_limited_images, 200),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            image.increaseSelectCount()
            selectedImages.add(newImage)
            EventBus.getDefault().post(ImageSelectedChangedEvent(position))
        }
    }

    fun goToEditImage(url: String, position: Int) {
        imagePosition = position
        openActivityForResult(
            EditImageActivity.getInstance(draft, url, this),
            REQUEST_EDIT_IMAGE
        )
    }

    fun removeImageFromSwapScreen(image: Image) {
        selectedImages.remove(image)
        lifecycleScope.launchWhenResumed { saveDraft() }
        (getFragmentByTag(SwapsFragment.TAG) as SwapsFragment?)?.dataChanged()
    }

    fun showMissingImage() {
        Toast.makeText(
            this,
            getString(R.string.requite_images),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun zoomImage(url: String) {
        (getFragmentByTag(SwapsFragment.TAG) as SwapsFragment?)?.zoomImage(url)
    }

    fun editImageDone() {
        (getFragmentByTag(SwapsFragment.TAG) as SwapsFragment?)?.editImageDone()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EDIT_IMAGE && resultCode == Activity.RESULT_OK) {
            editImageDone()
            val imageUrl = data!!.getStringExtra(EXTRA_EDITED_IMAGE_URL)!!
            if (imagePosition != -1) {
                selectedImages[imagePosition].url = imageUrl
                (getFragmentByTag(SwapsFragment.TAG) as SwapsFragment?)?.editImageDone(imagePosition)
            }
        }
    }

    override fun releaseData() {
        adLoadDialog?.dismiss()
    }

    private fun scanImageFolders() = ioScope.launch {

        //
        state = STATE_LOADING
        EventBus.getDefault().post(ImageLoadStateChangedEvent())
        albums.clear()
        val imageList = arrayListOf<Image>()
        val albumSet = mutableSetOf<String>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val query = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Images.Media.DATE_ADDED + " DESC"
        )
        query?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameImage =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val urlImage =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val albumId = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val albumNamehihi =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                val id = cursor.getLong(idIndex)
                val name: String? = cursor.getString(nameImage)
                val albumIdString: String? = cursor.getString(albumId)
                val albumNames: String? = cursor.getString(albumNamehihi)
                val url = cursor.getString(urlImage)
                url?.let {
                    val extension = url.substring(it.lastIndexOf(".") + 1)
                    if (MyStatic.ACCEPT_IMAGE_EXTENSIONS.contains(extension) && albumIdString != null) {
                        albumSet.add(albumIdString)
                        imageList.add(Image(id, albumIdString, albumNames, it, name))
                    }
                }
            }
        }
        val albumAll = Album("-1", getString(R.string.all_images))
        albumAll.imageList = imageList
        if (imageList.isNotEmpty()) {
            albumAll.imageUrl = imageList[0].url
            albums.add(albumAll)
        }

        albumSet.forEach { albumId ->
            val album = Album(albumId)
            imageList.forEach { image ->
                if (image.albumId == albumId) {
                    val albumName = image.albumName
                    if (albumName != null && albumName.isNotEmpty()) {
                        album.name = albumName
                    } else {
                        album.name = getString(R.string.unknown_album)
                    }
                    album.imageList.add(image)
                }
            }
            if (album.imageList.isNotEmpty()) {
                album.imageUrl = album.imageList[0].url
                albums.add(album)
            }
        }
        state = STATE_LOADED
        EventBus.getDefault().post(ImageLoadStateChangedEvent())
    }

    suspend fun saveDraft(): Boolean = CoroutineScope(Dispatchers.IO).async {
        try {
            if (selectedImages.size < 2) {
                return@async false
            } else {
                draft.images = selectedImages
                draft.totalDuration =
                    MyApplication.getInstance().videoDataState.calculateDurationWithImages(
                        selectedImages.size
                    )
                draftRepository.saveAsDraft(draft)
                return@async true
            }
        } catch (ex: Exception) {
            return@async false
        }
    }.await()

    override fun onSaveAsDraft() {
        lifecycleScope.launchWhenResumed {
            if (!saveDraft()) {
                draftRepository.deleteDraft(draft.id)
            } else {
                Toast.makeText(this@SelectActivity, "Saved to your drafts!", Toast.LENGTH_SHORT)
                    .show()
            }
            startActivity(MainActivity.getCallingIntent(this@SelectActivity))
            finish()
        }
    }

    override fun onDiscard() {
        lifecycleScope.launchWhenResumed {
            draftRepository.deleteDraft(draft.id)
            finish()
        }
    }

}