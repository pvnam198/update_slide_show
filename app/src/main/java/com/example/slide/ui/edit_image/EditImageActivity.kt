package com.example.slide.ui.edit_image

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.example.slide.database.entities.Draft
import com.example.slide.event.EditImageLoadedEvent
import com.example.slide.ui.edit_image.crop.CropImageFragment
import com.example.slide.ui.edit_image.fragment.*
import com.example.slide.ui.edit_image.framework.*
import com.example.slide.ui.edit_image.framework.splash.SplashFloatingItem
import com.example.slide.ui.edit_image.interfaces.OnSaveBitmap
import com.example.slide.ui.edit_image.utils.AssetUtils
import com.example.slide.ui.edit_image.utils.FilterUtils
import com.example.slide.ui.select_image.SelectActivity
import com.example.slide.util.BitmapUtil
import com.example.slide.util.FileUtils
import com.example.slide.util.StickerProvider
import com.example.slide.videolib.VideoConfig
import kotlinx.android.synthetic.main.activity_edit_image.*
import kotlinx.android.synthetic.main.edit_image_action_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.wysaid.nativePort.CGENativeLibrary
import org.wysaid.nativePort.CGENativeLibrary.LoadImageCallback
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class EditImageActivity : BaseActivity(), View.OnClickListener {

    private val uiScope = CoroutineScope(Dispatchers.Main)

    private var urlImage: String = ""

    private lateinit var mPhotoEditor: PhotoEditor

    private var currentBitmap: Bitmap? = null

    var originBitmap: Bitmap? = null

    private lateinit var draft: Draft

    private var isBlurBackgroundLoaded = false

    companion object {

        private const val EXTRA_IMAGES = "images"

        private const val EXTRA_DRAFT = "draft"

        fun getInstance(draft: Draft, urlImage: String, context: Context): Intent {
            val intent = Intent(context, EditImageActivity::class.java)
            intent.putExtra(EXTRA_IMAGES, urlImage)
            intent.putExtra(EXTRA_DRAFT, draft)
            return intent
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_IMAGES, urlImage)
        outState.putParcelable(EXTRA_DRAFT, draft)
    }

    override fun extractData(bundle: Bundle) {
        super.extractData(bundle)
        urlImage = bundle.getString(EXTRA_IMAGES) as String
        draft = bundle.getParcelable<Draft>(EXTRA_DRAFT) as Draft
    }

    override fun initViewTools() = InitViewTools({ R.layout.activity_edit_image })

    override fun initConfiguration(savedInstanceState: Bundle?) {
        super.initConfiguration(savedInstanceState)
        loadBitmap()
        CGENativeLibrary.setLoadImageCallback(loadImageCallback, null)
        mPhotoEditor = PhotoEditor.Builder(photo_editor_view)
            .build()
    }

    override fun initListener() {
        super.initListener()
        btn_submit.setOnClickListener(this)
        btn_back.setOnClickListener(this)
        btn_crop.setOnClickListener(this)
        btn_adjust.setOnClickListener(this)
        btn_overlay.setOnClickListener(this)
        btn_text.setOnClickListener(this)
        btn_emoji.setOnClickListener(this)
        btn_blur.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view?.id == R.id.btn_back) {
            onBackPressed()
        } else if (currentBitmap == null) {
            Toast.makeText(this, R.string.msg_loading_image, Toast.LENGTH_SHORT).show()
        } else {
            when (view?.id) {
                R.id.btn_text -> {
                    openTextFragment()
                }
                R.id.btn_submit -> {
                    saveSticker()
                }
                R.id.btn_emoji -> {
                    openEmojiTab()
                }
                R.id.btn_adjust -> {
                    openAdjustTab()
                }
                R.id.btn_overlay -> {
                    openOverlayTab()
                }
                R.id.btn_crop -> {
                    openCropTab()
                }
                R.id.btn_blur -> {
                    openBlurTab()
                }
            }
        }

    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack()
        else
            DiscardChangesDialogFragment.getInstance()
                .show(supportFragmentManager, DiscardChangesDialogFragment.TAG)
    }

    fun discardChanges() {
        val inputMethodManager: InputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(btn_back.windowToken, 0)
        finish()
    }

    private fun openOverlayTab() {
        photo_editor_view.setHandlingFloatingItem(null)
        imageEditMode()
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_full, OverlayFragment(), OverlayFragment::class.java.name)
            .addToBackStack(null)
            .commit()
    }

    private fun openAdjustTab() {
        photo_editor_view.setHandlingFloatingItem(null)
        imageEditMode()
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_full, AdjustFragment(), AdjustFragment::class.java.name)
            .addToBackStack(null)
            .commit()
    }

    private fun openTextFragment() {
        imageEditMode()
        photo_editor_view.setHandlingFloatingItem(null)
        photo_editor_view.setLocked(false)
        supportFragmentManager.beginTransaction()
            .add(R.id.root_view, TextFragment(), TextFragment::class.java.name)
            .addToBackStack(null)
            .commit()
    }

    fun addText(textSticker: TextFloatingItem) {
        iv_submit.visibility = View.VISIBLE
        btn_submit.visibility = View.VISIBLE
        if (textSticker.addTextProperties.text.isNotEmpty()) {
            photo_editor_view.addSticker(textSticker)
        }
    }

    fun updateText(textSticker: TextFloatingItem) {
        iv_submit.visibility = View.VISIBLE
        btn_submit.visibility = View.VISIBLE
        if (textSticker.addTextProperties.text.isNotEmpty()) {
            photo_editor_view.setHandlingFloatingItem(textSticker)
        }
    }

    private fun openEmojiTab() {
        photo_editor_view.setHandlingFloatingItem(null)
        imageEditMode()
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_full, StickerFragment(), StickerFragment::class.java.name)
            .addToBackStack(null)
            .commit()
    }

    override fun initTask() {
        super.initTask()
        photo_editor_view.icons = StickerProvider.getTextStickerIcons(this)
        photo_editor_view.setBackgroundColor(Color.BLACK)
        photo_editor_view.setLocked(false)
        photo_editor_view.isConstrained = true
        photo_editor_view.onStickerOperationListener =
            object : StickerView.OnStickerOperationListener {
                override fun onStickerAdded(floatingItem: FloatingItem) {}

                override fun onTouchUpForBeauty(x: Float, y: Float) {
                }

                override fun onStickerZoomFinished(floatingItem: FloatingItem) {
                }

                override fun onTouchDownForBeauty(x: Float, y: Float) {
                }

                override fun onStickerClicked(floatingItem: FloatingItem) {
                    if (floatingItem is TextFloatingItem) {
                        floatingItem.setTextColor(Color.RED)
                        photo_editor_view.replace(floatingItem)
                        photo_editor_view.invalidate()
                    }
                }

                override fun onStickerTouchedDown(floatingItem: FloatingItem) {

                }

                override fun onStickerFlipped(floatingItem: FloatingItem) {

                }

                override fun onStickerDeleted(floatingItem: FloatingItem) {

                }

                override fun onStickerTouchOutside() {

                }

                override fun onStickerDoubleTapped(floatingItem: FloatingItem) {
                    if (floatingItem is TextFloatingItem) {
                        editTextContents(floatingItem)
                    }
                }

                override fun onStickerDragFinished(floatingItem: FloatingItem) {

                }

                override fun onTouchDragForBeauty(x: Float, y: Float) {

                }
            }
    }

    private fun editTextContents(sticker: TextFloatingItem) {
        supportFragmentManager.beginTransaction()
            .add(R.id.root_view, TextFragment.getInstance(sticker), TextFragment::class.java.name)
            .addToBackStack(null)
            .commit()
    }

    override fun releaseData() {
        CGENativeLibrary.setLoadImageCallback(null, null)
        mPhotoEditor.clearAllViews()
    }

    private fun saveImg(img: Bitmap, draftId: Long) {
        val dir = FileUtils.getImagesTempDir(this, draftId)
        val nameImage = "${System.currentTimeMillis()}.jpg"
        val file = File(dir, nameImage)
        val fileOutputStream = FileOutputStream(file)
        if (img.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)) {
            fileOutputStream.close()
            setResult(Activity.RESULT_OK, SelectActivity.getIntent(file.absolutePath))
            finish()
        }
    }

    fun setEmoji(bitmap: Bitmap) {
        photo_editor_view.setHandlingFloatingItem(null)
        photo_editor_view.addSticker(
            DrawableFloatingItem(
                BitmapDrawable(resources, bitmap)
            )
        )
    }

    fun saveAndContinueEditImage() {
        lockImageEdit()
        var bitmap: Bitmap? = null
        photo_editor_view.glSurfaceView.alpha = 0F
        photo_editor_view.floatingItems?.let {
            mPhotoEditor.saveStickerAsBitmap(object : OnSaveBitmap {
                override fun onFailure(e: Exception?) {}
                override fun onBitmapReady(saveBitmap: Bitmap?) {
                    saveBitmap?.let {
                        bitmap = it
                        setCurrentBitmap(it)
                        photo_editor_view.setImageSource(bitmap)
                        photo_editor_view.glSurfaceView.alpha = 1F
                        photo_editor_view.floatingItems.clear()
                        showButtonSave()
                    }
                }
            })
        }
    }

    private fun lockImageEdit() {
        photo_editor_view.setHandlingFloatingItem(null)
        photo_editor_view.setLocked(true)
    }

    private fun showButtonSave() {
        btn_submit.visibility = View.VISIBLE
        iv_submit.visibility = View.VISIBLE
        linear_action.visibility = View.VISIBLE
    }

    //task
    private fun saveSticker() {
        photo_editor_view.floatingItems?.let {
            photo_editor_view.setHandlingFloatingItem(null)
            photo_editor_view.glSurfaceView.alpha = 0F
            mPhotoEditor.saveStickerAsBitmap(object : OnSaveBitmap {
                override fun onFailure(e: Exception?) {}
                override fun onBitmapReady(saveBitmap: Bitmap?) {
                    saveBitmap?.let {
                        setCurrentBitmap(it)
                        saveImg(it, draft.id)
                    }
                }
            })
        }
    }

    private fun imageEditMode() {
        photo_editor_view.setLocked(false)
        btn_submit.visibility = View.INVISIBLE
        iv_submit.visibility = View.INVISIBLE
    }

    fun normalMode() {
        photo_editor_view.setHandlingFloatingItem(null)
        photo_editor_view.setLocked(true)
        btn_submit.visibility = View.VISIBLE
        iv_submit.visibility = View.VISIBLE
        linear_action.visibility = View.VISIBLE
    }

    fun popFragment(fragmentTag: String) {
        if (canChangeFragmentManagerState()) {
            val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
            fragment?.let {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
        }
    }

    fun removeSticker() {
        onBackPressed()
        normalMode()
        photo_editor_view.removeBitmapStickers()
    }

    /*Adjust contents*/
    fun setAdjustFilter(filterConfig: String) {
        mPhotoEditor.setAdjustFilter(filterConfig)
    }

    fun saveAdjustAsBitmap() {
        photo_editor_view.saveGLSurfaceViewAsBitmap(object : OnSaveBitmap {
            override fun onFailure(e: java.lang.Exception?) {}
            override fun onBitmapReady(saveBitmap: Bitmap?) {
                uiScope.launch {
                    saveBitmap?.let {
                        photo_editor_view.setImageSource(saveBitmap)
                        photo_editor_view.setFilterEffect("")
                        setCurrentBitmap(it)
                    }
                    showButtonSave()
                }
            }
        })
    }

    fun closeAdjust() {
        photo_editor_view.setFilterEffect("")
        btn_submit.visibility = View.VISIBLE
        iv_submit.visibility = View.VISIBLE
        linear_action.visibility = View.VISIBLE
    }

    /*Overlay contents*/
    fun setCurrentBitmap(bitmap: Bitmap) {
        currentBitmap = bitmap
    }

    fun getCurrentBitmap(): Bitmap? {
        return currentBitmap
    }

    fun setImageOverlay(config: String) {
        mPhotoEditor.setFilterEffect(config)
        photo_editor_view.glSurfaceView.setFilterIntensity(0.7f)
    }

    fun saveImageOverLay() {
        photo_editor_view.saveGLSurfaceViewAsBitmap(object : OnSaveBitmap {
            override fun onFailure(e: java.lang.Exception?) {
            }

            override fun onBitmapReady(saveBitmap: Bitmap?) {
                CoroutineScope(Dispatchers.Main).launch {
                    saveBitmap?.let {
                        setCurrentBitmap(saveBitmap)
                        photo_editor_view.setImageSource(saveBitmap)
                        photo_editor_view.setFilterEffect("")
                    }
                    onBackPressed()
                    btn_submit.visibility = View.VISIBLE
                    iv_submit.visibility = View.VISIBLE
                }
            }
        })
    }

    fun setFilterIntensity(intensity: Float) {
        photo_editor_view.setFilterIntensity(intensity)
    }

    fun cancelOverlay() {
        onBackPressed()
        mPhotoEditor.setFilterEffect("")
    }

    /*Blur contents*/
    private fun setImageBlur(bitmap: Bitmap) {
        Log.d("kimkakaedit", "setImageBlur")
        splash_view.setImageBitmap(bitmap)
    }

    fun addBlurSticker(blurMask: BlurProvider.BlurMask?) {
        blurMask?.let {
            CoroutineScope(Dispatchers.IO).launch {
                if (!isBlurBackgroundLoaded){
                    isBlurBackgroundLoaded = true
                    val blurBitmapBackground = FilterUtils.getBlurImageFromBitmap(currentBitmap, 3F)
                    CoroutineScope(Dispatchers.Main).launch {
                        blurBitmapBackground?.let {
                            splash_view.setImageBitmap(it)
                        }
                    }
                }
                val splash =
                    SplashFloatingItem(
                        AssetUtils.loadBitmapFromAssets(
                            this@EditImageActivity,
                            it.maskAsset
                        ), AssetUtils.loadBitmapFromAssets(this@EditImageActivity, it.shadowAsset)
                    )
                CoroutineScope(Dispatchers.Main).launch {
                    splash_view.addSticker(splash)
//                    splash_view.refreshDrawableState()
//                    splash_view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                }
            }
        }
        setVisibleBlurView(blurMask != null)

    }

    fun toggleSaveButton(visibility: Boolean) {
        if (visibility) {
            btn_submit.visibility = View.VISIBLE
            iv_submit.visibility = View.VISIBLE
        } else {
            btn_submit.visibility = View.INVISIBLE
            iv_submit.visibility = View.INVISIBLE
        }
    }

    private fun openBlurTab() {
        isBlurBackgroundLoaded = false
        photo_editor_view.setHandlingFloatingItem(null)
        toggleSaveButton(false)
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_full, BlurFragment(), BlurFragment::class.java.name)
            .commit()
    }

    private fun togglePhotoEditorView(isVisible: Boolean) {
        if (isVisible) {
            wrap_photo_view.visibility = View.VISIBLE
        } else {
            wrap_photo_view.visibility = View.GONE
        }
    }

    private fun setVisibleBlurView(isVisible: Boolean) {
        if (isVisible) {
            cst_blur.visibility = View.VISIBLE
        } else {
            cst_blur.visibility = View.GONE
        }
    }

    fun saveImageBlur() {
        val bitmap = splash_view.getBitmap(getCurrentBitmap())
        setCurrentBitmap(bitmap)
        photo_editor_view.setImageSource(bitmap)
        popFragment(BlurFragment::class.java.name)
        togglePhotoEditorView(true)
        setVisibleBlurView(false)
        toggleSaveButton(true)
    }

    fun cancelBlurImageEdit() {
        popFragment(BlurFragment::class.java.name)
        togglePhotoEditorView(true)
        setVisibleBlurView(false)
        toggleSaveButton(true)
    }

    fun changeValueBlur(value: Float) {
        progress_bar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = FilterUtils.getBlurImageFromBitmap(
                currentBitmap,
                value
            )

            CoroutineScope(Dispatchers.Main).launch {
                bitmap?.let {
                    setImageBlur(it)
                }
                progress_bar.visibility = View.INVISIBLE
            }
        }
    }

    private fun openCropTab() {
        photo_editor_view.setHandlingFloatingItem(null)
        supportFragmentManager.beginTransaction().replace(
            R.id.root_view,
            CropImageFragment(),
            CropImageFragment::class.java.name
        ).addToBackStack(null).commit()
    }


    fun saveImageCrop(bitmap: Bitmap) {
        setCurrentBitmap(bitmap)
        photo_editor_view.setImageSource(bitmap)
        onBackPressed()
    }

    fun showVipEmojiDialog() {
        VipEmojiDialogFragment
            .createInstance()
            .show(supportFragmentManager, VipEmojiDialogFragment.TAG)
    }

    private fun loadBitmap() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val originalBitmap =
                    Glide.with(this@EditImageActivity).asBitmap().centerInside()
                        .load(urlImage)
                        .override(VideoConfig.VIDEO_WIDTH_1080, VideoConfig.VIDEO_HEIGHT_1080)
                        .submit().get()
                val backgroundBitmap = originalBitmap.copy(originalBitmap.config, false)
                val blurBitmap: Bitmap = BitmapUtil.blur(backgroundBitmap, this@EditImageActivity)
                val bitmap = BitmapUtil.bitmapOneOverBackground(originalBitmap, blurBitmap)
                originBitmap = bitmap
                blurBitmap.recycle()
                backgroundBitmap.recycle()
                setCurrentBitmap(bitmap)
                val blurBitmapBackground = FilterUtils.getBlurImageFromBitmap(currentBitmap, 3F)
                CoroutineScope(Dispatchers.Main).launch {
                    blurBitmapBackground?.let {
                        splash_view.setImageBitmap(it)
                    }
                    photo_editor_view.initialBitmap(bitmap)
                    progress_bar.visibility = View.GONE
                    EventBus.getDefault().post(EditImageLoadedEvent())
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        this@EditImageActivity,
                        R.string.msg_image_deleted,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    finish()
                }
            }
        }

    }

    private var loadImageCallback: LoadImageCallback = object : LoadImageCallback {
        override fun loadImage(name: String?, arg: Any?): Bitmap? {
            val am = assets
            val inputStream: InputStream
            inputStream = try {
                am.open(name!!)
            } catch (e: IOException) {
                return null
            }

            return BitmapFactory.decodeStream(inputStream)
        }

        override fun loadImageOK(bmp: Bitmap?, arg: Any?) {

        }

    }

}