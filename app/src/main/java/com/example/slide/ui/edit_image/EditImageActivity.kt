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
import com.example.slide.databinding.ActivityEditImageBinding
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

class EditImageActivity : BaseActivity<ActivityEditImageBinding>(), View.OnClickListener {

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

    override fun bindingView(): ActivityEditImageBinding {
        return ActivityEditImageBinding.inflate(layoutInflater)
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
        mPhotoEditor = PhotoEditor.Builder(binding.photoEditorView)
            .build()
    }

    override fun initListener() {
        super.initListener()
        binding.btnSubmit.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
        binding.editImageAction.btnCrop.setOnClickListener(this)
        binding.editImageAction.btnAdjust.setOnClickListener(this)
        binding.editImageAction.btnOverlay.setOnClickListener(this)
        binding.editImageAction.btnText.setOnClickListener(this)
        binding.editImageAction.btnEmoji.setOnClickListener(this)
        binding.editImageAction.btnBlur.setOnClickListener(this)
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
        inputMethodManager.hideSoftInputFromWindow(binding.btnBack.windowToken, 0)
        finish()
    }

    private fun openOverlayTab() {
        binding.photoEditorView.setHandlingFloatingItem(null)
        imageEditMode()
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_full, OverlayFragment(), OverlayFragment::class.java.name)
            .addToBackStack(null)
            .commit()
    }

    private fun openAdjustTab() {
        binding.photoEditorView.setHandlingFloatingItem(null)
        imageEditMode()
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_full, AdjustFragment(), AdjustFragment::class.java.name)
            .addToBackStack(null)
            .commit()
    }

    private fun openTextFragment() {
        imageEditMode()
        binding.photoEditorView.setHandlingFloatingItem(null)
        binding.photoEditorView.setLocked(false)
        supportFragmentManager.beginTransaction()
            .add(R.id.root_view, TextFragment(), TextFragment::class.java.name)
            .addToBackStack(null)
            .commit()
    }

    fun addText(textSticker: TextFloatingItem) {
        binding.ivSubmit.visibility = View.VISIBLE
        binding.btnSubmit.visibility = View.VISIBLE
        if (textSticker.addTextProperties.text.isNotEmpty()) {
            binding.photoEditorView.addSticker(textSticker)
        }
    }

    fun updateText(textSticker: TextFloatingItem) {
        binding.ivSubmit.visibility = View.VISIBLE
        binding.btnSubmit.visibility = View.VISIBLE
        if (textSticker.addTextProperties.text.isNotEmpty()) {
            binding.photoEditorView.setHandlingFloatingItem(textSticker)
        }
    }

    private fun openEmojiTab() {
        binding.photoEditorView.setHandlingFloatingItem(null)
        imageEditMode()
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_full, StickerFragment(), StickerFragment::class.java.name)
            .addToBackStack(null)
            .commit()
    }

    override fun initTask() {
        super.initTask()
        binding.photoEditorView.icons = StickerProvider.getTextStickerIcons(this)
        binding.photoEditorView.setBackgroundColor(Color.BLACK)
        binding.photoEditorView.setLocked(false)
        binding.photoEditorView.isConstrained = true
        binding.photoEditorView.onStickerOperationListener =
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
                        binding.photoEditorView.replace(floatingItem)
                        binding.photoEditorView.invalidate()
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
        binding.photoEditorView.setHandlingFloatingItem(null)
        binding.photoEditorView.addSticker(
            DrawableFloatingItem(
                BitmapDrawable(resources, bitmap)
            )
        )
    }

    fun saveAndContinueEditImage() {
        lockImageEdit()
        var bitmap: Bitmap? = null
        binding.photoEditorView.glSurfaceView.alpha = 0F
        binding.photoEditorView.floatingItems?.let {
            mPhotoEditor.saveStickerAsBitmap(object : OnSaveBitmap {
                override fun onFailure(e: Exception?) {}
                override fun onBitmapReady(saveBitmap: Bitmap?) {
                    saveBitmap?.let {
                        bitmap = it
                        setCurrentBitmap(it)
                        binding.photoEditorView.setImageSource(bitmap)
                        binding.photoEditorView.glSurfaceView.alpha = 1F
                        binding.photoEditorView.floatingItems.clear()
                        showButtonSave()
                    }
                }
            })
        }
    }

    private fun lockImageEdit() {
        binding.photoEditorView.setHandlingFloatingItem(null)
        binding.photoEditorView.setLocked(true)
    }

    private fun showButtonSave() {
        binding.btnSubmit.visibility = View.VISIBLE
        binding.ivSubmit.visibility = View.VISIBLE
        binding.editImageAction.linearAction.visibility = View.VISIBLE
    }

    //task
    private fun saveSticker() {
        binding.photoEditorView.floatingItems?.let {
            binding.photoEditorView.setHandlingFloatingItem(null)
            binding.photoEditorView.glSurfaceView.alpha = 0F
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
        binding.photoEditorView.setLocked(false)
        binding.btnSubmit.visibility = View.INVISIBLE
        binding.ivSubmit.visibility = View.INVISIBLE
    }

    fun normalMode() {
        binding.photoEditorView.setHandlingFloatingItem(null)
        binding.photoEditorView.setLocked(true)
        binding.btnSubmit.visibility = View.VISIBLE
        binding.ivSubmit.visibility = View.VISIBLE
        binding.editImageAction.linearAction.visibility = View.VISIBLE
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
        binding.photoEditorView.removeBitmapStickers()
    }

    /*Adjust contents*/
    fun setAdjustFilter(filterConfig: String) {
        mPhotoEditor.setAdjustFilter(filterConfig)
    }

    fun saveAdjustAsBitmap() {
        binding.photoEditorView.saveGLSurfaceViewAsBitmap(object : OnSaveBitmap {
            override fun onFailure(e: java.lang.Exception?) {}
            override fun onBitmapReady(saveBitmap: Bitmap?) {
                uiScope.launch {
                    saveBitmap?.let {
                        binding.photoEditorView.setImageSource(saveBitmap)
                        binding.photoEditorView.setFilterEffect("")
                        setCurrentBitmap(it)
                    }
                    showButtonSave()
                }
            }
        })
    }

    fun closeAdjust() {
        binding.photoEditorView.setFilterEffect("")
        binding.btnSubmit.visibility = View.VISIBLE
        binding.ivSubmit.visibility = View.VISIBLE
        binding.editImageAction.linearAction.visibility = View.VISIBLE
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
        binding.photoEditorView.glSurfaceView.setFilterIntensity(0.7f)
    }

    fun saveImageOverLay() {
        binding.photoEditorView.saveGLSurfaceViewAsBitmap(object : OnSaveBitmap {
            override fun onFailure(e: java.lang.Exception?) {
            }

            override fun onBitmapReady(saveBitmap: Bitmap?) {
                CoroutineScope(Dispatchers.Main).launch {
                    saveBitmap?.let {
                        setCurrentBitmap(saveBitmap)
                        binding.photoEditorView.setImageSource(saveBitmap)
                        binding.photoEditorView.setFilterEffect("")
                    }
                    onBackPressed()
                    binding.btnSubmit.visibility = View.VISIBLE
                    binding.ivSubmit.visibility = View.VISIBLE
                }
            }
        })
    }

    fun setFilterIntensity(intensity: Float) {
        binding.photoEditorView.setFilterIntensity(intensity)
    }

    fun cancelOverlay() {
        onBackPressed()
        mPhotoEditor.setFilterEffect("")
    }

    /*Blur contents*/
    private fun setImageBlur(bitmap: Bitmap) {
        Log.d("kimkakaedit", "setImageBlur")
        binding.splashView.setImageBitmap(bitmap)
    }

    fun addBlurSticker(blurMask: BlurProvider.BlurMask?) {
        blurMask?.let {
            CoroutineScope(Dispatchers.IO).launch {
                if (!isBlurBackgroundLoaded){
                    isBlurBackgroundLoaded = true
                    val blurBitmapBackground = FilterUtils.getBlurImageFromBitmap(currentBitmap, 3F)
                    CoroutineScope(Dispatchers.Main).launch {
                        blurBitmapBackground?.let {
                            binding.splashView.setImageBitmap(it)
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
                    binding.splashView.addSticker(splash)
//                    binding.splashView.refreshDrawableState()
//                    binding.splashView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                }
            }
        }
        setVisibleBlurView(blurMask != null)

    }

    fun toggleSaveButton(visibility: Boolean) {
        if (visibility) {
            binding.btnSubmit.visibility = View.VISIBLE
            binding.ivSubmit.visibility = View.VISIBLE
        } else {
            binding.btnSubmit.visibility = View.INVISIBLE
            binding.ivSubmit.visibility = View.INVISIBLE
        }
    }

    private fun openBlurTab() {
        isBlurBackgroundLoaded = false
        binding.photoEditorView.setHandlingFloatingItem(null)
        toggleSaveButton(false)
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_full, BlurFragment(), BlurFragment::class.java.name)
            .commit()
    }

    private fun togglePhotoEditorView(isVisible: Boolean) {
        if (isVisible) {
            binding.wrapPhotoView.visibility = View.VISIBLE
        } else {
            binding.wrapPhotoView.visibility = View.GONE
        }
    }

    private fun setVisibleBlurView(isVisible: Boolean) {
        if (isVisible) {
            binding.cstBlur.visibility = View.VISIBLE
        } else {
            binding.cstBlur.visibility = View.GONE
        }
    }

    fun saveImageBlur() {
        val bitmap = binding.splashView.getBitmap(getCurrentBitmap())
        setCurrentBitmap(bitmap)
        binding.photoEditorView.setImageSource(bitmap)
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
        binding.progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = FilterUtils.getBlurImageFromBitmap(
                currentBitmap,
                value
            )

            CoroutineScope(Dispatchers.Main).launch {
                bitmap?.let {
                    setImageBlur(it)
                }
                binding.progressBar.visibility = View.INVISIBLE
            }
        }
    }

    private fun openCropTab() {
        binding.photoEditorView.setHandlingFloatingItem(null)
        supportFragmentManager.beginTransaction().replace(
            R.id.root_view,
            CropImageFragment(),
            CropImageFragment::class.java.name
        ).addToBackStack(null).commit()
    }


    fun saveImageCrop(bitmap: Bitmap) {
        setCurrentBitmap(bitmap)
        binding.photoEditorView.setImageSource(bitmap)
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
                        binding.splashView.setImageBitmap(it)
                    }
                    binding.photoEditorView.initialBitmap(bitmap)
                    binding.progressBar.visibility = View.GONE
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