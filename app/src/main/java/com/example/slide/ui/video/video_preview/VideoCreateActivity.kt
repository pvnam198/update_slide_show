package com.example.slide.ui.video.video_preview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.slide.MyApplication
import com.example.slide.R
import com.example.slide.ads.*
import com.example.slide.base.InitViewTools
import com.example.slide.base.tabhost.TabController
import com.example.slide.base.tabhost.TabInfo
import com.example.slide.common.Common
import com.example.slide.database.entities.Draft
import com.example.slide.database.entities.FloatingAddedEntity
import com.example.slide.database.entities.FloatingStickerEntity
import com.example.slide.database.entities.FloatingTextEntity
import com.example.slide.databinding.ActivityVideoCreateBinding
import com.example.slide.event.VideoProgressStateChanged
import com.example.slide.framework.texttovideo.DrawableVideoFloatingItem
import com.example.slide.framework.texttovideo.TextListToVideoView
import com.example.slide.framework.texttovideo.VideoTextFloatingItem
import com.example.slide.local.PreferencesHelper
import com.example.slide.model.Image
import com.example.slide.music_engine.CropMusic
import com.example.slide.music_engine.DefaultMusic
import com.example.slide.ui.create.SaveDraftDialog
import com.example.slide.ui.edit_image.framework.AddTextProperties
import com.example.slide.ui.edit_image.framework.FloatingItem
import com.example.slide.ui.home.MainActivity
import com.example.slide.ui.select_image.SelectActivity
import com.example.slide.ui.select_music.model.Track
import com.example.slide.ui.video.video_export.ExportVideoActivity
import com.example.slide.ui.video.video_preview.callback.HandleFloatItem
import com.example.slide.ui.video.video_preview.dialog.AddTextToVideoBottomDialogFragment
import com.example.slide.ui.video.video_preview.dialog.VideoQualityDialogFragment
import com.example.slide.ui.video.video_preview.dialog.VipFunctionsDialogFragment
import com.example.slide.ui.video.video_preview.fragments.*
import com.example.slide.ui.video.video_preview.model.DataPreview
import com.example.slide.ui.video.video_preview.model.VideoFrame
import com.example.slide.util.StringUtils
import com.example.slide.videolib.VideoConfig
import com.google.android.gms.ads.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.min


class VideoCreateActivity : MultiMusicPlayingActivity(), View.OnClickListener,
    SeekBar.OnSeekBarChangeListener, SaveDraftDialog.OnSaveDraftListener {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun initViewTools() = InitViewTools({ R.layout.activity_video_create }, { true })

    private var isVipFeature = false

    private var adLoadDialog: AdLoadingDialog? = null

    private var isNeedRestartService = false

    private var banner: AdView? = null

    private var isVip = false

    private lateinit var tabControl: TabController

    private val editImageFragment = EditImageFragment()

    private val subtitleFragment = SubtitleFragment()

    private var loadingDialog: AlertDialog? = null

    private var currentTab = POS_TRANSITION

    private var frameSelectedPosition = 0

    var isHideButtonSave = false

    var isLoaded = false

    private val preferencesHelper by lazy {
        PreferencesHelper(this)
    }

    private var mode = MODE_NEW_EDIT

    companion object {

        const val EXTRA_START_MODE = "mode"

        const val MODE_NEW_EDIT = 0

        const val MODE_EDIT_DRAFT = 1

        private const val POS_TRANSITION = 0

        private const val POS_FRAME = 1

        private const val POS_EDIT_IMAGE = 2

        private const val POS_MUSIC = 3

        private const val POS_SUBTITLE = 4

        private const val POS_DURATION = 5

        private const val POS_EMOJI = 6

        private const val EXTRA_TRACK = "track"

        private const val EXTRA_DEFAULT_MUSIC = "default_music"

        private const val EXTRA_DRAFT = "draft"

        const val EXTRA_DATA_PREVIEW = "data_review"

        const val EXTRA_IMAGES = "images"

        private var firstImage = ""

        const val ACTIVITY_NAME = "VideoCreateActivity::class.java.name"

        private const val ARG_FRAME_POSITION_SELECTED = "frame_selected_position"

        private const val ARG_CURRENT_POSITION = "current_position"

        private const val ARG_IS_HIDE_BUTTON_SAVE = "is_hide_button_save"

        fun getFirstImage(): String {
            return firstImage
        }

        fun getIntent(context: Context, images: ArrayList<Image>): Intent {
            val intent = Intent(context, VideoCreateActivity::class.java)
            intent.putExtra(EXTRA_IMAGES, images)
            return intent
        }

        fun getIntent(track: Track): Intent {
            val intent = Intent()
            intent.putExtra(EXTRA_TRACK, track)
            return intent
        }

        fun getIntent(defaultMusic: DefaultMusic): Intent {
            val intent = Intent()
            intent.putExtra(EXTRA_DEFAULT_MUSIC, defaultMusic)
            return intent
        }

        fun getIntent(context: Context, draft: Draft, mode: Int = MODE_NEW_EDIT): Intent {
            val intent = Intent(context, VideoCreateActivity::class.java)
            intent.putExtra(EXTRA_DRAFT, draft)
            intent.putExtra(EXTRA_START_MODE, mode)
            return intent
        }
    }

    private suspend fun loadFloatingItemFromDraft(draft: Draft): ArrayList<FloatingItem> =
        CoroutineScope(Dispatchers.IO).async {
            val floatingItems = ArrayList<FloatingItem>()
            draft.floatingItemsAdded.forEach { floatingAddedItem ->
                if (floatingAddedItem is FloatingStickerEntity) {
                    val sticker = getDrawableVideoFloatingItem(floatingAddedItem)
                    floatingItems.add(sticker)
                } else if (floatingAddedItem is FloatingTextEntity) {
                    floatingItems.add(getTextFloatingItem(floatingAddedItem))
                }
            }
            return@async floatingItems
        }.await()

    private fun getTextFloatingItem(floatingAddedItem: FloatingTextEntity): VideoTextFloatingItem {
        val addTextProperties = AddTextProperties.getDefaultProperties()
        addTextProperties.text = floatingAddedItem.text
        addTextProperties.textWidth = floatingAddedItem.textWidth
        addTextProperties.textHeight = floatingAddedItem.textHeight
        addTextProperties.textColor = floatingAddedItem.textColor
        addTextProperties.fontName = floatingAddedItem.fontName
        addTextProperties.textSize = floatingAddedItem.textSize

        val videoTextSticker =
            VideoTextFloatingItem(
                this@VideoCreateActivity,
                addTextProperties
            )
        videoTextSticker.isFullTime = floatingAddedItem.isFullTime
        videoTextSticker.startTime = floatingAddedItem.startTime
        videoTextSticker.endTime = floatingAddedItem.endTime

        val matrix = Matrix()
        matrix.setValues(floatingAddedItem.matrixValues)
        videoTextSticker.setMatrix(matrix)
        return videoTextSticker
    }

    private suspend fun getDrawableVideoFloatingItem(floatingStickerEntity: FloatingStickerEntity) =
        suspendCoroutine<DrawableVideoFloatingItem> {
            Glide.with(this).asBitmap().load(floatingStickerEntity.iconPath)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val matrix = Matrix()
                        matrix.setValues(floatingStickerEntity.matrixValues)
                        val drawableVideoSticker =
                            DrawableVideoFloatingItem(
                                BitmapDrawable(
                                    this@VideoCreateActivity.resources, resource
                                ),
                                floatingStickerEntity.iconPath,
                                floatingStickerEntity.startTime,
                                floatingStickerEntity.endTime,
                            ).apply {
                                setMatrix(matrix)
                                isFullTime = floatingStickerEntity.isFullTime
                            }
                        it.resume(drawableVideoSticker)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ARG_CURRENT_POSITION, currentTab)
        outState.putInt(ARG_FRAME_POSITION_SELECTED, frameSelectedPosition)
        outState.putSerializable(EXTRA_DATA_PREVIEW, dataPreview)
        outState.putParcelable(EXTRA_DRAFT, draft)
        outState.putInt(EXTRA_START_MODE, mode)
        outState.putBoolean(ARG_IS_HIDE_BUTTON_SAVE, isHideButtonSave)
    }

    override fun extractData(bundle: Bundle) {
        super.extractData(bundle)
        mode = bundle.getInt(EXTRA_START_MODE)
        currentTab = bundle.getInt(ARG_CURRENT_POSITION)
        frameSelectedPosition = bundle.getInt(ARG_FRAME_POSITION_SELECTED)
        draft = bundle.getParcelable<Draft>(EXTRA_DRAFT) as Draft
        dataPreview = buildDataPreviewFromDraft(draft)
        isHideButtonSave = bundle.getBoolean(ARG_IS_HIDE_BUTTON_SAVE)
    }

    private suspend fun initWithDraft(draft: Draft) {
        val existDraft = draftRepository.getDraft(draft.id)
        if (existDraft.isNullOrEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.msg_draft_is_valid),
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        } else {
            this@VideoCreateActivity.draft = existDraft[0]
        }
        val stickerFromDraft = loadFloatingItemFromDraft(this@VideoCreateActivity.draft)
        findViewById<TextListToVideoView>(R.id.text_list_to_video_view).getAndAddStickers(
            stickerFromDraft
        )
    }

    override fun onResume() {
        super.onResume()
        Log.d("kimkakatest", "onResume")
        banner?.resume()
        if (isLoaded) {
            loadingDialog?.dismiss()
        }
    }

    override fun onPause() {
        Log.d("kimkakatest", "onPause")
        banner?.pause()
        super.onPause()
    }


    override fun onRestart() {
        super.onRestart()
        VideoConfig.setupVideoQuality(VideoConfig.VIDEO_QUALITY_480)
    }

    private fun buildDataPreviewFromDraft(draft: Draft): DataPreview {
        return DataPreview(
            draft.images,
            draft.texts,
            draft.videoFrame,
            ThemeProvider.getThemeById(draft.themeId)
        ).apply { synchronizedList(draft.cropMusic) }
            .also {
                if (it.cropMusics.isEmpty() && mode == MODE_NEW_EDIT) {
                    it.addCropMusic(CropMusic(defaultMusic = ThemeProvider.SHUFFLE_EFFECT.defaultMusic))
                }
            }
    }

    override fun initConfiguration(savedInstanceState: Bundle?) {
        super.initConfiguration(savedInstanceState)
        isVip = PreferencesHelper(this).isVip()
        if (isHideButtonSave) {
            binding.btnCheck.visibility = View.INVISIBLE
        } else {
            binding.btnCheck.visibility = View.VISIBLE
        }
        initLoadingDialog()
        initVideoFrame()
        initVideoState()
        initBanner()
        tabControl = TabController(supportFragmentManager, arrayOf(
            TabInfo(
                binding.frameVideoEffect,
                binding.btnTransition,
                TransitionFragment::class.java.name
            ) { TransitionFragment() },
            TabInfo(
                binding.frameVideoFrame,
                binding.btnSelectFrame,
                FrameFragment::class.java.name
            ) { FrameFragment() },
            TabInfo(
                binding.frameVideoEdit,
                binding.btnEditImages,
                EditImageFragment::class.java.name
            ) { editImageFragment },
            TabInfo(
                binding.frameVideoMusic,
                binding.btnAddMusic,
                EditMusicFragment::class.java.name
            ) { EditMusicFragment() },
            TabInfo(
                binding.frameVideoSubtitle,
                binding.btnSubtitle,
                SubtitleFragment::class.java.name
            ) { subtitleFragment },
            TabInfo(
                binding.frameVideoDuration,
                binding.btnDuration,
                DurationFragment::class.java.name
            ) { DurationFragment() },
            TabInfo(
                binding.frameStickerVideo,
                binding.btnEmoji,
                StickerInVideoFragment::class.java.name
            ) { StickerInVideoFragment() }
        ))
    }

    private fun initLoadingDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@VideoCreateActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.dialog_loading_text)
        loadingDialog = builder.create()
    }

    private fun initVideoFrame() {
        dataPreview.videoFrame?.let {
            Glide.with(this).load(it.getUri())
                .into(binding.ivBgFrame)
        }
    }

    override fun initVideoState() {
        myApplication.videoDataState.initBaseWithImageNumber(dataPreview.images.size)
        firstImage = dataPreview.images[0].url
        binding.seekBar.max = myApplication.videoDataState.totalImageFrame
        binding.tvEndTime.text =
            StringUtils.getDurationDisplayFromSeconds(myApplication.videoDataState.totalSecond)
        binding.seekBar.secondaryProgress = myApplication.videoDataState.outputImages.size
        super.initVideoState()
    }

    override fun initListener() {
        super.initListener()
        binding.seekBar.setOnSeekBarChangeListener(this)
        binding.btnTransition.setOnClickListener(this)
        binding.btnSelectFrame.setOnClickListener(this)
        binding.btnEditImages.setOnClickListener(this)
        binding.btnAddMusic.setOnClickListener(this)
        binding.btnDuration.setOnClickListener(this)
        binding.btnSubtitle.setOnClickListener(this)
        binding.btnEmoji.setOnClickListener(this)
        binding.ivToggleVideo.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
        binding.btnCheck.setOnClickListener(this)
        binding.btnVip.setOnClickListener(this)
        binding.textListToVideoView.onStickerOperationListener = onStickerOperationListener
    }

    override fun initTask() {
        Log.d("kimkakatest", "initTask")
        lifecycleScope.launchWhenCreated {
            Log.d("kimkakatest", "launchWhenCreated")
            loadingDialog?.show()
            initWithDraft(draft)
            isLoaded = true
            dataPreview.let {
                PrepareVideoService.enqueueCreateImage(
                    this@VideoCreateActivity,
                    it,
                    draft
                )
            }
            binding.seekBar.secondaryProgress = myApplication.videoDataState.outputImages.size
            if (canChangeFragmentManagerState()) {
                tabControl.openTab(currentTab)
                loadingDialog?.dismiss()
            }
            super.initTask()
        }
    }

    private fun initBanner() {
        if (isVip) return
        if (!Ads.isShowAds) return
        banner = AdView(this)
        BannerHelper.loadAdaptiveBanner(this, banner!!, binding.layoutAdsParent, binding.layoutAds, binding.tvLoading)
    }

    fun addTextToVideo(
        videoTextSticker: VideoTextFloatingItem
    ) {
        binding.textListToVideoView.addSticker(videoTextSticker)
        binding.textListToVideoView.setHandlingFloatingItem(videoTextSticker)
        handleFloatingItem?.onFloatingItemChangedEvent()
    }

    fun updateSub(videoTextSticker: VideoTextFloatingItem) {
        if (videoTextSticker.addTextProperties.text.isNotEmpty()) {
            binding.textListToVideoView.setHandlingFloatingItem(videoTextSticker)
        } else {
            removeSub(videoTextSticker)
        }
        handleFloatingItem?.onFloatingItemChangedEvent()
    }


    override fun releaseData() {
        super.releaseData()
        loadingDialog?.dismiss()
        binding.textListToVideoView.onStickerOperationListener = null
        banner?.destroy()
    }

    override fun bindingView(): ActivityVideoCreateBinding {
        return ActivityVideoCreateBinding.inflate(layoutInflater)
    }

    private fun setIsVipFeature(isVip: Boolean) {
        isVipFeature = isVip
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_transition -> {
                binding.textListToVideoView.setHandlingFloatingItem(null)
                currentTab = POS_TRANSITION
                tabControl.openTab(POS_TRANSITION)
            }
            R.id.btn_select_frame -> {
                binding.textListToVideoView.setHandlingFloatingItem(null)
                currentTab = POS_FRAME
                tabControl.openTab(POS_FRAME)
            }
            R.id.btn_edit_images -> {
                binding.textListToVideoView.setHandlingFloatingItem(null)
                currentTab = POS_EDIT_IMAGE
                tabControl.openTab(POS_EDIT_IMAGE)
            }
            R.id.btn_add_music -> {
                binding.textListToVideoView.setHandlingFloatingItem(null)
                currentTab = POS_MUSIC
                tabControl.openTab(POS_MUSIC)
            }
            R.id.btn_duration -> {
                binding.textListToVideoView.setHandlingFloatingItem(null)
                currentTab = POS_DURATION
                tabControl.openTab(POS_DURATION)
            }
            R.id.btn_subtitle -> {
                currentTab = POS_SUBTITLE
                tabControl.openTab(POS_SUBTITLE)
            }
            R.id.btn_emoji -> {
                currentTab = POS_EMOJI
                tabControl.openTab(POS_EMOJI)
            }
            R.id.iv_toggle_video -> {
                toggleVideo()
            }
            R.id.btn_back -> {
                binding.textListToVideoView.floatingTextItems.forEach {
                    Timber.d(it.text ?: "Not Text")
                }

                onBackPressed()
            }
            R.id.btn_check -> {
                dataPreview.let {
                    pauseVideo()
                    VideoQualityDialogFragment.getInstance(it)
                        .show(supportFragmentManager, VideoQualityDialogFragment.TAG)
                }
            }
            R.id.btn_vip -> {
                showVipDialog(VipFunctionsDialogFragment.TASK_UNLOCK)
            }
        }
    }

    private fun showVipDialog(task: Int, videoQuality: Int = VideoConfig.VIDEO_QUALITY_480) {
        pauseVideo()
        VipFunctionsDialogFragment.getInstance(isVipFeature, videoQuality, task)
            .show(supportFragmentManager, VipFunctionsDialogFragment.TAG)
    }

    override fun onBackPressed() {
        binding.btnCheck.visibility = View.VISIBLE
        isHideButtonSave = false
        when {
            supportFragmentManager.backStackEntryCount > 0 -> super.onBackPressed()
            mode == MODE_NEW_EDIT -> SaveDraftDialog.createInstance()
                .show(supportFragmentManager, SaveDraftDialog.TAG)
            mode == MODE_EDIT_DRAFT -> {
                onSaveAsDraft()
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            val newProgress = min(progress, seekBar.secondaryProgress)
            onSeek(newProgress)
            if (progress > seekBar.secondaryProgress)
                seekBar.progress = newProgress
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        pauseVideo()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        playVideo(true)
    }

    override fun onStart() {
        Log.d("kimkakatest", "onStart")
        isVip = PreferencesHelper(this).isVip()
        super.onStart()
        if (isVip) {
            binding.layoutAdsParent.visibility = View.GONE
            requestToUseNormalFeature()
        } else {
        }
        if (isNeedRestartService) {
            restartPlayingService()
            isNeedRestartService = false
        } else {
            initVideoState()
        }
    }

    override fun onStop() {
        Log.d("kimkakatest", "onStop")
        super.onStop()
        pauseVideo()
    }

    fun goSwapImages() {
        pauseVideo()
        val intent =
            SelectActivity.getIntent(dataPreview.images, draft, SelectActivity.MODE_SWAP, this)
        InterstitialHelperV2.showInterstitialAd(this, object : OnInterstitialCallback {
            override fun onWork() {
                startActivityForResult(intent, Common.REQUEST_SWAP_IMAGES)
            }
        })
    }

    fun goAddNewImages() {
        pauseVideo()
        val intent =
            SelectActivity.getIntent(dataPreview.images, draft, SelectActivity.MODE_ADD_IMAGE, this)
        InterstitialHelperV2.showInterstitialAd(this, object : OnInterstitialCallback {
            override fun onWork() {
                startActivityForResult(intent, Common.REQUEST_ADD_IMAGES)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("kimkakaac", "onActivityResult")
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Common.REQUEST_SWAP_IMAGES -> {
                    dataPreview.images =
                        data!!.getSerializableExtra(EXTRA_IMAGES) as ArrayList<Image>
                    restartPlayingService(true)
                }
                Common.REQUEST_ADD_IMAGES -> {
                    dataPreview.images =
                        data!!.getSerializableExtra(EXTRA_IMAGES) as ArrayList<Image>
                    restartPlayingService(true)
                }
                Common.REQUEST_PICK_AUDIO -> {
                    val track: Track? = data!!.getSerializableExtra(EXTRA_TRACK) as Track?
                    track?.let {
                        addCropMusic(track = it)
                    }
                    val defaultMusic: DefaultMusic? =
                        data.getSerializableExtra(EXTRA_DEFAULT_MUSIC) as DefaultMusic?
                    defaultMusic?.let {
                        addCropMusic(defaultMusic = it)
                    }
                }
                Common.REQUEST_GO_TO_VIP -> {
                    requestToUseNormalFeature()
                }
            }
        }
    }

    fun restartPlayingService(isNeedUpdateImages: Boolean = false) {
        onSeek(0)
        lifecycleScope.launchWhenResumed { saveDraft() }
        VideoConfig.setupVideoQuality(VideoConfig.VIDEO_QUALITY_480)
        stopVideo()
        if (isNeedUpdateImages) {
            editImageFragment.notifyAdapter()
        }
        binding.progressLoading.visibility = View.VISIBLE
        initVideoState()
        PrepareVideoService.enqueueCreateImage(this, dataPreview, draft)
        playVideo()
    }

    fun applyTheme(newTheme: ThemeProvider.Theme): Boolean {
        if (newTheme.id != dataPreview.selectedTheme.id) {
            val oldTheme = dataPreview.selectedTheme
            dataPreview.selectedTheme = newTheme
            setIsVipFeature(newTheme.isPremium)
            if (PreferencesHelper(this).isVipOrVipTrialMember())
                binding.layoutVip.visibility = View.INVISIBLE
            else
                binding.layoutVip.visibility = if (newTheme.isPremium) View.VISIBLE else View.INVISIBLE
            val cropMusic = onMusicThemeChanged(oldTheme, newTheme)
            if (cropMusic != null) {
                dataPreview.synchronizedListFromTheme(cropMusic)
            }
            restartPlayingService()
            return true
        }
        return false
    }

    fun setFrameForImagePreview(videoFrame: VideoFrame, position: Int) {
        lifecycleScope.launchWhenResumed { saveDraft() }
        var uriFrame = Uri.parse("")
        if (videoFrame.id != 0) {
            uriFrame = videoFrame.getUri()
            dataPreview.videoFrame = videoFrame
            frameSelectedPosition = position
        } else {
            dataPreview.videoFrame = null
        }
        Glide.with(this).load(uriFrame)
            .into(binding.ivBgFrame)
    }

    fun getFrameSelectedPosition(): Int {
        return frameSelectedPosition
    }

    fun setDurationChanged(duration: Int) {
        Log.d("fdsfs", "setDurationChanged")
        lifecycleScope.launchWhenResumed { saveDraft() }
        if (VideoConfig.getSecondPerImageAnimate() != duration) {
            val isChanged = VideoConfig.setSecondPerImageAnimate(duration, myApplication)
            preferencesHelper.saveDuration(duration)
            if (isChanged)
                restartPlayingService()
            else
                initVideoState()
        }

        val totalTime = MyApplication.getInstance().videoDataState.totalSecond
        getVideoTextStickers().forEach {
            if (it.endTime > totalTime){
                Log.d("fdsfs", "change endtime: ")
                it.endTime = totalTime
            }

            if (it.startTime > totalTime){
                Log.d("fdsfs", "change start and end time ")
                it.startTime = 0
                it.endTime = totalTime
            }
        }
        getDrawableVideoStickers().forEach {
            if (it.endTime > totalTime){
                it.endTime = totalTime
            }

            if (it.startTime > totalTime){
                it.startTime = 0
                it.endTime = totalTime
            }
        }

        subtitleFragment.durationChanged()
    }

    fun requestToUseNormalFeature() {
        binding.layoutVip.visibility = View.INVISIBLE
    }

    fun saveVideoPreview(videoQuality: Int): Boolean {
        if (videoQuality != VideoConfig.VIDEO_QUALITY_480) {
            if (!PreferencesHelper(this).isVipOrVipTrialMember()) {
                showVipDialog(VipFunctionsDialogFragment.TASK_SAVE, videoQuality)
            } else {
                isNeedRestartService = true
                launchSaveVideoActivity(videoQuality)
                return true
            }
        } else {
            if (!isVipFeature || PreferencesHelper(this).isVipOrVipTrialMember()) {
                launchSaveVideoActivity(videoQuality)
                return true
            } else {
                showVipDialog(VipFunctionsDialogFragment.TASK_SAVE, videoQuality)
            }
        }
        return false
    }

    fun showAddTextFragment() {
        AddTextToVideoBottomDialogFragment.getInstance()
            .show(supportFragmentManager, AddTextToVideoBottomDialogFragment.TAG)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onProgressChanged(event: VideoProgressStateChanged) {
        binding.seekBar?.secondaryProgress = myApplication.videoDataState.outputImages.size
    }


    fun editSubTitle(videoTextSticker: VideoTextFloatingItem) {
        videoTextSticker.isShow = false
        binding.textListToVideoView.setHandlingFloatingItem(null)
        val dialog = AddTextToVideoBottomDialogFragment.getInstance(videoTextSticker)
        dialog.show(supportFragmentManager, AddTextToVideoBottomDialogFragment.TAG)
    }

    fun removeSub(videoTextSticker: VideoTextFloatingItem) {
        binding.textListToVideoView.removeSticker(videoTextSticker.id)
    }

    fun getVideoTextStickers(): ArrayList<VideoTextFloatingItem> {
        return binding.textListToVideoView.floatingTextItems
    }

    fun getDrawableVideoStickers(): ArrayList<DrawableVideoFloatingItem> {
        return binding.textListToVideoView.drawableVideoSticker
    }

    fun launchSaveVideoActivity(videoQuality: Int) = ioScope.launch {
        uiScope.launch { loadingDialog?.show() }
        VideoConfig.setupVideoQuality(videoQuality)
        dataPreview.texts = binding.textListToVideoView.getVideoTextExports(videoQuality)
        uiScope.launch {
            myApplication.videoDataState.isCancel = false
            loadingDialog?.dismiss()
            val intent =
                ExportVideoActivity.getInstance(
                    this@VideoCreateActivity,
                    dataPreview,
                    draft,
                    videoQuality,
                    mode
                )
            InterstitialHelperV2.showInterstitialAd(
                this@VideoCreateActivity,
                object : OnInterstitialCallback {
                    override fun onWork() {
                        startActivity(intent)
                    }
                })
        }
    }

    fun setEmoji(drawableVideoSticker: DrawableVideoFloatingItem) {
        binding.textListToVideoView.addSticker(drawableVideoSticker)
        handleFloatingItem?.onFloatingItemChangedEvent()
    }

    fun openStickerFragment() {
        binding.btnCheck.visibility = View.INVISIBLE
        isHideButtonSave = true
        val videoStickerFragment = EmojiVideoFragment()
        supportFragmentManager.beginTransaction()
            .add(binding.frameEmojiVideo, videoStickerFragment, EmojiVideoFragment.TAG)
            .addToBackStack(null).commit()
    }

    fun openChangeTimeVideoStickerDialog(drawableVideoSticker: DrawableVideoFloatingItem) {
        ChangeTimeVideoStickerFragment.newInstance(drawableVideoSticker)
            .show(supportFragmentManager, ChangeTimeVideoStickerFragment.TAG)
    }

    fun removeDrawableVideoSticker(drawableVideoSticker: DrawableVideoFloatingItem) {
        binding.textListToVideoView.removeDrawableVideoSicker(drawableVideoSticker.id)
        handleFloatingItem?.onFloatingItemChangedEvent()
    }

    var handleFloatingItem: HandleFloatItem? = null

    private val onStickerOperationListener = object :
        TextListToVideoView.OnStickerOperationListener {
        override fun onStickerAdded(floatingItem: FloatingItem) {
            CoroutineScope(Dispatchers.Main).launch { saveDraft() }
        }

        override fun onStickerClicked(floatingItem: FloatingItem) {
            CoroutineScope(Dispatchers.Main).launch { saveDraft() }
        }

        override fun onStickerDeleted(floatingItem: FloatingItem) {
            handleFloatingItem?.onFloatingItemChangedEvent()
        }

        override fun onStickerTouchOutside() {
            CoroutineScope(Dispatchers.Main).launch { saveDraft() }
        }

        override fun onStickerDragFinished(floatingItem: FloatingItem) {
            CoroutineScope(Dispatchers.Main).launch { saveDraft() }
        }

        override fun onStickerTouchedDown(floatingItem: FloatingItem) {
            CoroutineScope(Dispatchers.Main).launch { saveDraft() }
        }

        override fun onStickerZoomFinished(floatingItem: FloatingItem) {
        }

        override fun onStickerFlipped(floatingItem: FloatingItem) {

        }

        override fun onStickerDoubleTapped(floatingItem: FloatingItem) {
            if (floatingItem is VideoTextFloatingItem) {
                editSubTitle(floatingItem)
            } else if (floatingItem is DrawableVideoFloatingItem) {
                openChangeTimeVideoStickerDialog(floatingItem)
            }
        }

        override fun onTouchDownForBeauty(x: Float, y: Float) {
        }

        override fun onTouchDragForBeauty(x: Float, y: Float) {
        }

        override fun onTouchUpForBeauty(x: Float, y: Float) {
        }

    }

    suspend fun saveDraft(): Boolean = CoroutineScope(Dispatchers.IO).async {
        try {
            Timber.d("Saving draft")
            if (dataPreview.images.size < 2) {
                return@async false
            } else {
                draft.images = dataPreview.images
                draft.totalDuration =
                    MyApplication.getInstance().videoDataState.calculateDurationWithImages(
                        dataPreview.images.size
                    )
                draft.texts = dataPreview.texts
                draft.videoFrame = dataPreview.videoFrame
                draft.themeId = dataPreview.selectedTheme.id
                draft.floatingItemsAdded =
                    buildFloatingAddedList(binding.textListToVideoView.floatingItems)
                draft.cropMusic = dataPreview.cropMusics
                draftRepository.saveAsDraft(draft)
                Timber.d("Saved Draft: $draft")
                return@async true
            }
        } catch (ex: Exception) {
            Timber.d("Saved Draft Failed: ${ex.message}")
            return@async false
        }
    }.await()

    override fun onSaveAsDraft() {
        lifecycleScope.launchWhenResumed {
            if (saveDraft()) {
                Toast.makeText(
                    this@VideoCreateActivity,
                    getString(R.string.saved_to_your_draft),
                    Toast.LENGTH_SHORT
                ).show()
            }
            startActivity(MainActivity.getCallingIntent(this@VideoCreateActivity))
            finish()
        }
    }

    private fun buildFloatingAddedList(floatingItems: ArrayList<FloatingItem>): ArrayList<FloatingAddedEntity> {
        val floatingAddedEntities = ArrayList<FloatingAddedEntity>()
        floatingItems.forEach {
            when (it) {
                is VideoTextFloatingItem -> {
                    floatingAddedEntities.add(
                        FloatingTextEntity(
                            textWidth = it.textWidth,
                            textHeight = it.textHeight,
                            textAlpha = it.textAlpha,
                            textColor = it.textColor,
                            fontName = it.addTextProperties.fontName,
                            textSize = it.addTextProperties.textSize,
                            paddingHeight = it.paddingHeight,
                            paddingWidth = it.paddingWidth,
                            backgroundAlpha = it.backgroundAlpha,
                            backgroundColor = it.backgroundColor,
                            backgroundBorder = it.backgroundBorder,
                            isShowBackground = it.isShowBackground,
                            id = it.id,
                            text = it.text,
                            isHandling = false
                        ).apply {
                            this.matrixValues = it.matrixValues
                            this.startTime = it.startTime
                            this.endTime = it.endTime
                            this.isFullTime = it.isFullTime
                        })
                }
                is DrawableVideoFloatingItem -> {
                    floatingAddedEntities.add(FloatingStickerEntity(it.iconPath).apply {
                        this.startTime = it.startTime
                        this.endTime = it.endTime
                        this.isFullTime = it.isFullTime
                        this.matrixValues = it.matrixValues
                    })
                }
            }
        }
        return floatingAddedEntities
    }

    override fun onDiscard() {
        lifecycleScope.launchWhenResumed {
            draftRepository.deleteDraft(draft.id)
            startActivity(MainActivity.getCallingIntent(this@VideoCreateActivity))
            finish()
        }
    }

}