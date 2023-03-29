package com.example.slide.ui.video.video_export

import Jni.FFmpegCmd
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.ads.Ads
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.example.slide.data.SharkVideoDao
import com.example.slide.database.entities.Draft
import com.example.slide.databinding.ActivityExportVideoBinding
import com.example.slide.event.BeginSavingVideoEvent
import com.example.slide.event.ExportErrorEvent
import com.example.slide.event.VideoCreatedEvent
import com.example.slide.event.VideoProgressStateChanged
import com.example.slide.local.PreferencesHelper
import com.example.slide.repository.DraftRepository
import com.example.slide.ui.home.MainActivity
import com.example.slide.ui.my_studio.MyVideo
import com.example.slide.ui.video.video_preview.PrepareVideoService
import com.example.slide.ui.video.video_preview.ThemeProvider
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.ui.video.video_preview.model.DataPreview
import com.example.slide.ui.video.video_share.ShareVideoActivity
import com.example.slide.util.*
import com.example.slide.videolib.VideoConfig
import com.example.slide.videolib.VideoDataState
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.io.File
import java.util.*

var currentNativeAd: NativeAd? = null

class ExportVideoActivity : BaseActivity<ActivityExportVideoBinding>(), View.OnClickListener,
    SaveDraftAfterExportDialog.OnSaveDraftListener {

    override fun initViewTools() = InitViewTools({ R.layout.activity_export_video }, { true })


    override fun bindingView(): ActivityExportVideoBinding {
        return ActivityExportVideoBinding.inflate(layoutInflater)
    }

    private var mode = MODE_NEW_EDIT

    companion object {

        private const val TAG = "Exportctvity123"

        const val EXTRA_VIDEO = "video"

        private const val EXTRA_DATA_PREVIEW = "data_preview"

        private const val EXTRA_DRAFT = "draft"

        private const val EXTRA_START_MODE = "mode"

        const val MODE_NEW_EDIT = 0

        const val MODE_EDIT_DRAFT = 1

        fun getInstance(
            context: Context,
            dataPreview: DataPreview,
            draft: Draft,
            videoQuality: Int,
            mode: Int = VideoCreateActivity.MODE_NEW_EDIT
        ): Intent {
            val intent = Intent(context, ExportVideoActivity::class.java)
            intent.putExtra(EXTRA_DATA_PREVIEW, dataPreview)
            intent.putExtra(VideoConfig.VIDEO_QUALITY, videoQuality)
            intent.putExtra(EXTRA_DRAFT, draft)
            intent.putExtra(EXTRA_START_MODE, mode)
            return intent
        }
    }

    private lateinit var draftRepository: DraftRepository

    private var isVip = false

    private var video: MyVideo? = null

    private lateinit var dataPreview: DataPreview

    private lateinit var draft: Draft

    private val timer = Timer()

    private val tickRunnable = object : TimerTask() {
        override fun run() {
            runOnUiThread {
                binding.seekBar.progress = binding.videoView.currentPosition
                binding.tvStartTime.text =
                    StringUtils.getDurationDisplayFromMillis(binding.videoView.currentPosition)
            }
        }
    }

    private var videoQuality = VideoConfig.VIDEO_QUALITY_480

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(EXTRA_VIDEO, video)
        outState.putSerializable(EXTRA_DATA_PREVIEW, dataPreview)
        outState.putInt(VideoConfig.VIDEO_QUALITY, videoQuality)
        outState.putInt(EXTRA_START_MODE, mode)
        outState.putParcelable(EXTRA_DRAFT, draft)
        Log.d(TAG, "onSaveInstanceState: ")
    }

    override fun extractData(bundle: Bundle) {
        super.extractData(bundle)
        video = bundle.getSerializable(EXTRA_VIDEO) as MyVideo?
        draft = bundle.getParcelable<Draft>(EXTRA_DRAFT) as Draft
        dataPreview = bundle.getSerializable(EXTRA_DATA_PREVIEW) as DataPreview
        videoQuality = bundle.getInt(VideoConfig.VIDEO_QUALITY)
        mode = bundle.getInt(EXTRA_START_MODE)
        Log.d(TAG, "extractData: ")
    }

    override fun releaseData() {
        timer.cancel()
        binding.videoView.stopPlayback()
        currentNativeAd?.destroy()
        currentNativeAd = null
    }

    override fun initTask() {
        super.initTask()
        if (video == null) {
            val videoState = myApplication.videoDataState
            if (videoQuality != VideoConfig.VIDEO_QUALITY_480) {
                videoState.resetDoneState()
            }
            if (videoQuality != VideoConfig.VIDEO_QUALITY_480 ||
                videoState.state == VideoDataState.STATE_NONE
            ) {
                if (!videoState.isPreparing(videoQuality) && !videoState.isPreparingDone(
                        videoQuality
                    )
                ) {
                    Log.d("kimkakaservice3", "preparing " + videoQuality)
                    PrepareVideoService.enqueueCreateImage(this, dataPreview, draft, videoQuality)
                }
            }
            if (!videoState.isExportingVideo) {
                Log.d("kimkakaservice3", "exporting " + videoQuality)
                SavingVideoService.enqueueSaveVideo(this, dataPreview, videoQuality)
            }
        } else {
            initVideo(video!!)
        }
        timer.scheduleAtFixedRate(tickRunnable, 300, 300)
        Log.d(TAG, "initTask: ")
    }

    override fun initConfiguration(savedInstanceState: Bundle?) {
        super.initConfiguration(savedInstanceState)
        isVip = PreferencesHelper(this).isVip()
        draftRepository = DraftRepository(this)
        Glide.with(this).load(VideoCreateActivity.getFirstImage()).into(binding.ivSaving)
        initProgressState()
        loadNativeAds()
        if (Utils.isScopeStorage()) {
            binding.frameRename.visibility = View.GONE
        } else {
            binding.frameRename.visibility = View.VISIBLE
        }
        Log.d(TAG, "initConfiguration: ")
    }

    override fun initListener() {
        super.initListener()
        binding.btnBack.setOnClickListener(this)
        binding.btnHome.setOnClickListener(this)
        binding.btnToggleVideo.setOnClickListener(this)
        binding.btnRename.setOnClickListener(this)
        binding.btnDelete.setOnClickListener(this)
        binding.videoShare.btnTwitter.setOnClickListener(this)
        binding.videoShare.btnInstagram.setOnClickListener(this)
        binding.videoShare.btnFacebook.setOnClickListener(this)
        binding.videoShare.btnYoutube.setOnClickListener(this)
        binding.videoShare.btnShareMore.setOnClickListener(this)

        binding.videoView.setOnPreparedListener { mediaPlayer ->
            binding.seekBar.max = mediaPlayer.duration
            binding.tvEndTime.text = StringUtils.getDurationDisplayFromMillis(mediaPlayer.duration)
            binding.videoView.start()
            binding.ivToggleVideo.setImageResource(R.drawable.ic_pause)
        }

        binding.videoView.setOnCompletionListener {
            binding.ivToggleVideo.setImageResource(R.drawable.ic_play)
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, process: Int, fromUser: Boolean) {
                if (fromUser) {
                    Timber.d("hehehez: $process")
                    Timber.d("hehehez: ${video!!.duration}")
                    binding.videoView.seekTo(process)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        Log.d(TAG, "initListener: ")
    }

    override fun onStart() {
        Log.d(TAG, "onStart: ")
        isVip = PreferencesHelper(this).isVip()
        super.onStart()
    }

    private fun buildDataPreviewFromDraft(draft: Draft): DataPreview {
        return DataPreview(
            draft.images,
            draft.texts,
            draft.videoFrame,
            ThemeProvider.getThemeById(draft.themeId)
        ).apply { synchronizedList(draft.cropMusic) }
    }

    private fun loadNativeAds() {
        if (isVip) return
        val builder = AdLoader.Builder(this, Ads.getNativeVideoAdsId())
        builder.forNativeAd { nativeAd ->
            // OnUnifiedNativeAdLoadedListener implementation.
            // If this callback occurs after the activity is destroyed, you must call
            // destroy and return or you may get a memory leak.
            if (isDestroyed) {
                nativeAd.destroy()
                return@forNativeAd
            }
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            currentNativeAd?.destroy()
            currentNativeAd = nativeAd
            val adView = layoutInflater
                .inflate(R.layout.item_ads_1, null) as NativeAdView
            populateUnifiedNativeAdView(nativeAd, adView)
            binding.layoutAdsParent.removeAllViews()
            binding.layoutAdsParent.addView(adView)
        }
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onProgressChanged(event: VideoProgressStateChanged) {
        initProgressState(event.process)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSavingError(event: ExportErrorEvent) {
        ExportFailedDialog().show(supportFragmentManager, ExportFailedDialog.TAG)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInitFileName(event: BeginSavingVideoEvent) {
        binding.tvHeaderTitle.text = event.fileName
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onVideoCreated(event: VideoCreatedEvent) {
        RateDialogFragment.show(this, preferencesHelper)
        initVideo(event.video)
        binding.ivHome.visibility = View.VISIBLE
        binding.btnHome.visibility = View.VISIBLE
    }


    @SuppressLint("SetTextI18n")
    private fun initProgressState(process: Int = 0) {
        when (myApplication.videoDataState.state) {
            VideoDataState.STATE_NONE -> {
                binding.tvState.text = "${this.resources.getText(R.string.preparing_images)}: 00%"
            }
            VideoDataState.STATE_CREATE_IMAGE -> {
                binding.tvState.text = "${this.resources.getText(R.string.preparing_images)}: ${process}%"
            }
            else -> {
                binding.tvState.text = "${this.resources.getText(R.string.exporting)}: ${process}%"
            }
        }
    }

    private fun initVideo(video: MyVideo) {
        this.video = video
        binding.videoView.setVideoPath(video.url)
        binding.btnHome.visibility = View.VISIBLE
        binding.ivHome.visibility = View.VISIBLE
        binding.layoutBlock.visibility = View.GONE
        binding.ivSaving.visibility = View.INVISIBLE
        binding.savingBlock.visibility = View.INVISIBLE
        binding.progress.visibility = View.INVISIBLE
        binding.tvState.visibility = View.INVISIBLE
    }

    override fun onBackPressed() {
        if (video == null) {
            Toast.makeText(this, R.string.msg_prepare_video, Toast.LENGTH_LONG).show()
        } else {
            super.onBackPressed()
        }
    }

    fun cancelExport() {
        FFmpegCmd.exit()
        FFmpegCmd.term_exit()
        myApplication.videoDataState.isCancel = true
        finish()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_back -> {
                onBackPressed()
            }
            R.id.btn_home -> {
                when (mode) {
                    MODE_NEW_EDIT -> {
                        SaveDraftAfterExportDialog().show(
                            supportFragmentManager,
                            SaveDraftAfterExportDialog.TAG
                        )
                    }
                    MODE_EDIT_DRAFT -> {
                        startActivity(MainActivity.getCallingIntent(this))
                    }
                }
            }
            R.id.btn_toggle_video -> {
                if (binding.videoView.isPlaying) {
                    binding.ivToggleVideo.setImageResource(R.drawable.ic_play)
                    binding.videoView.pause()
                } else {
                    binding.ivToggleVideo.setImageResource(R.drawable.ic_pause)
                    binding.videoView.start()
                }
            }
            R.id.btn_rename -> {
                showDialogRename()
            }
            R.id.btn_delete -> {
                deleteVideo()
            }
            R.id.btn_twitter -> {
                ShareUtils.createTwitterIntent(this, File(video!!.url))
            }
            R.id.btn_instagram -> {
                ShareUtils.createInstagramIntent(this, File(video!!.url))
            }
            R.id.btn_facebook -> {
                ShareUtils.createFacebookIntent(this, File(video!!.url))
            }
            R.id.btn_youtube -> {
                ShareUtils.createYoutubeIntent(this, File(video!!.url))
            }
            R.id.btn_share_more -> {
                ShareUtils.createShareMoreIntent(this, File(video!!.url))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        video?.let {
            if (requestCode == ShareVideoActivity.REQUEST_DELETE_FILE && resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, R.string.video_deleted, Toast.LENGTH_SHORT).show()
                SharkVideoDao.getInstance(this).deleteByVideoId(it.id)
                finish()
            }
        }
    }

    private fun deleteVideo() {
        if (Utils.isScopeStorage()) {
            val uris = ArrayList<Uri>()
            uris.add(
                ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    video!!.id
                )
            )
            MediaStore.createDeleteRequest(contentResolver, uris)
            val pendingIntent =
                MediaStore.createDeleteRequest(contentResolver, uris.filter {
                    checkUriPermission(
                        it,
                        Binder.getCallingPid(),
                        Binder.getCallingUid(),
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    ) != PackageManager.PERMISSION_GRANTED
                })
            startIntentSenderForResult(
                pendingIntent.intentSender,
                ShareVideoActivity.REQUEST_DELETE_FILE,
                null,
                0,
                0,
                0
            )
        } else {
            showDialogDeleteVideo()
        }
    }

    private fun showDialogDeleteVideo() {
        val dialog = AlertDialog.Builder(this)
            .setMessage(R.string.msg_delete_video)
            .setTitle(R.string.confirm_delete)
            .setPositiveButton(R.string.ok) { dialog, which ->
                val success = FileUtils.deleteVideoFromDevice(this, video!!.id)
                if (success) {
                    Toast.makeText(this, R.string.video_deleted, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, R.string.msg_cant_delete, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, whichButton ->
                dialog.cancel()
            }
            .create()
        dialog.show()
    }

    private fun showDialogRename() {
        val view = layoutInflater.inflate(R.layout.dialog_rename_video, null, false)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setTitle(R.string.rename_video)
            .setPositiveButton(
                R.string.ok
            ) { dialog, whichButton -> }
            .setNegativeButton(
                R.string.cancel
            ) { dialog, whichButton -> dialog.cancel() }.create()

        val titleEditText = view.findViewById<AppCompatEditText>(R.id.edt_name)
        titleEditText.setText(video!!.name)

        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                val title = titleEditText.text.toString()
                if (title == video!!.name) {
                    dialog.cancel()
                } else if (TextUtils.isEmpty(title)) {
                    Toast.makeText(this, R.string.please_enter_file_name, Toast.LENGTH_SHORT).show()
                    dialog.cancel()
                } else {
                    var url = video!!.url
                    if (url.lastIndexOf("/") != -1) {
                        url = url.substring(0, url.lastIndexOf("/") + 1)
                        url = "$url$title.mp4"
                        val file = File(url)
                        if (file.exists()) {
                            Toast.makeText(
                                this,
                                R.string.file_name_is_already_exists,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val success = File(video!!.url).renameTo(File(url))
                            if (success) {
                                Toast.makeText(
                                    this,
                                    R.string.file_successfully_renamed,
                                    Toast.LENGTH_SHORT
                                ).show()
                                video!!.url = url
                                video!!.name = title
                                VideoTagUtils.updateTag(this, video!!)
                                binding.tvHeaderTitle.text = video!!.name
                            } else {
                                Toast.makeText(
                                    this,
                                    R.string.file_not_successfully_renamed,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            dialog.cancel()
                        }
                    }
                }
            }
    }

    override fun onSaveAsDraft() {
        Toast.makeText(
            this@ExportVideoActivity,
            getString(R.string.saved_to_your_draft),
            Toast.LENGTH_SHORT
        ).show()
        startActivity(MainActivity.getCallingIntent(this@ExportVideoActivity))
    }

    override fun onDiscard() {
        lifecycleScope.launchWhenResumed {
            draftRepository.deleteDraft(draft.id)
            startActivity(MainActivity.getCallingIntent(this@ExportVideoActivity))
        }
    }

    private fun populateUnifiedNativeAdView(
        nativeAd: NativeAd,
        adView: NativeAdView
    ) {
        // Set the media view.
        adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

    }
}