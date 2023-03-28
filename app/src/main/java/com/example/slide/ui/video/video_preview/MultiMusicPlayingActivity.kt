package com.example.slide.ui.video.video_preview

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.MediaStoreSignature
import com.example.slide.R
import com.example.slide.base.BaseActivity
import com.example.slide.database.entities.Draft
import com.example.slide.event.MusicStateChangedEvent
import com.example.slide.music_engine.CropMusic
import com.example.slide.music_engine.DefaultMusic
import com.example.slide.music_engine.MultiMusicPlayBack
import com.example.slide.repository.DraftRepository
import com.example.slide.ui.select_music.model.Track
import com.example.slide.ui.video.video_preview.model.DataPreview
import com.example.slide.util.StringUtils
import com.example.slide.videolib.VideoConfig
import kotlinx.android.synthetic.main.activity_video_create.*
import org.greenrobot.eventbus.EventBus

abstract class MultiMusicPlayingActivity : BaseActivity() {

    companion object {
        val TAG = "kimkakavideoplaying"
    }

    private val displayImageRunnable = DisplayImageRunnable()

    protected lateinit var draftRepository: DraftRepository

    lateinit var dataPreview: DataPreview

    lateinit var draft: Draft

    private lateinit var requestManager: RequestManager

    private val handler: Handler by lazy { Handler(mainLooper) }

    override fun initConfiguration(savedInstanceState: Bundle?) {
        super.initConfiguration(savedInstanceState)
        draftRepository = DraftRepository(this)
        requestManager = Glide.with(this)
        displayImageRunnable.initMediaPlayer()
    }

    override fun initTask() {
        super.initTask()
        playVideo()
    }

    open fun initVideoState() {
        displayImage()
    }

    fun addCropMusic(defaultMusic: DefaultMusic? = null, track: Track? = null) {
        displayImageRunnable.addCropMusic(CropMusic(defaultMusic, track))
    }

    fun onSeek(newProgress: Int) {
        displayImageRunnable.setProgress(newProgress)
        displayImage()
    }

    fun pauseVideo() {
        if (displayImageRunnable.isPlaying()) {
            displayImageRunnable.pause()
            iv_toggle_video.setImageResource(R.drawable.ic_play)
        }
    }

    fun toggleVideo() {
        if (displayImageRunnable.isPlaying()) {
            displayImageRunnable.pause()
            iv_toggle_video.setImageResource(R.drawable.ic_play)
        } else {
            displayImageRunnable.play()
            iv_toggle_video.setImageResource(R.drawable.ic_pause)
        }
    }

    fun playVideo(isFromSeek: Boolean = false) {
        if (!displayImageRunnable.isPlaying()) {
            iv_toggle_video.setImageResource(R.drawable.ic_pause)
            displayImageRunnable.play(isFromSeek)
        }
    }

    fun stopVideo() {
        iv_toggle_video.setImageResource(R.drawable.ic_play)
        displayImageRunnable.stop()
    }

    //MUSIC
    fun onMusicThemeChanged(
        oldTheme: ThemeProvider.Theme,
        newTheme: ThemeProvider.Theme
    ): CropMusic? {
        return displayImageRunnable.onMusicThemeChanged(oldTheme, newTheme)
    }

    fun isPlaying() = displayImageRunnable.isPlaying()

    fun getCurrentMusicPos(): Int {
        return displayImageRunnable.getCurrentMusicPos()
    }

    fun removeMusicAtPosition(position: Int) {
        displayImageRunnable.removeMusicAtPosition(position)
    }

    fun getSelectedMusic(): ArrayList<CropMusic> {
        return displayImageRunnable.getSelectedMusic()
    }

    fun restoreDefaultMusic() {
        displayImageRunnable.restoreDefaultMusic()
    }

    private fun displayImage() {
        try {
            if (displayImageRunnable.getProgress() >= seek_bar.max) {
                displayImageRunnable.setProgress(seek_bar.max)
                tv_start_time.text =
                    StringUtils.getDurationDisplayFromFrame(displayImageRunnable.getProgress())
                pauseVideo()
            } else {
                if (displayImageRunnable.getProgress() < seek_bar.secondaryProgress) {
                    progress_loading.visibility = View.INVISIBLE
                    requestManager.asBitmap()
                        .load(myApplication.videoDataState.outputImages[displayImageRunnable.getProgress()])
                        .signature(
                            MediaStoreSignature(
                                "image/*",
                                System.currentTimeMillis(), 0
                            )
                        )
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(object :
                            SimpleTarget<Bitmap?>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap?>?
                            ) {
                                iv_frame.setImageBitmap(resource)
                            }
                        } as Target<Bitmap?>)
                    displayImageRunnable.increaseProgress()
                    seek_bar.progress = displayImageRunnable.getProgress()
                    tv_start_time.text =
                        StringUtils.getDurationDisplayFromFrame(displayImageRunnable.getProgress())
                } else {
                    progress_loading.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            requestManager = Glide.with(this)
        }
    }

    override fun releaseData() {
        displayImageRunnable.release()
    }

    inner class DisplayImageRunnable : Runnable {

        private var sProgress = 0

        private val playBack = MultiMusicPlayBack(this@MultiMusicPlayingActivity)

        var isPause = true

        override fun run() {
            if (!isPause) {
                text_list_to_video_view.setCurrentTime(getCurrentPlayingTimeInSecond())
                displayImage()
                handler.postDelayed(
                    displayImageRunnable,
                    (1000 / VideoConfig.getFps()).toLong()
                )
            }
        }

        fun initMediaPlayer() {
            dataPreview.let { playBack.initFromData(it, this@MultiMusicPlayingActivity) }
        }

        fun restoreDefaultMusic() {
            dataPreview.let {
                playBack.restoreMusicDefault(it, this@MultiMusicPlayingActivity)
                if (!isPause)
                    playBack.start()
            }
        }

        fun getProgress() = sProgress

        private fun getCurrentPlayingTimeInSecond() = sProgress / VideoConfig.getFps()

        fun play(isFromSeek: Boolean = false) {
            isPause = false
            if (sProgress == myApplication.videoDataState.totalImageFrame){
                playBack.seekFromPlayingDone()
            }
            if (!isFromSeek && sProgress == seek_bar.max) {
                playBack.stop()
                sProgress = 0
            }
            handler.postDelayed(displayImageRunnable, (1000 / VideoConfig.getFps()).toLong())
            playBack.start()
            EventBus.getDefault().post(MusicStateChangedEvent())
        }

        fun pause() {
            Log.d(TAG, "DisplayImageRunnable: pause")
            isPause = true
            playBack.pause()
            EventBus.getDefault().post(MusicStateChangedEvent())
        }

        fun stop() {
            Log.d(TAG, "DisplayImageRunnable: stop")
            pause()
            handler.removeCallbacksAndMessages(null)
            sProgress = 0
            seek_bar.progress = 0
            seek_bar.secondaryProgress = 0
        }

        fun release() {
            pause()
            sProgress = 0
            seek_bar.progress = sProgress
            playBack.release()
        }

        fun onMusicThemeChanged(
            oldTheme: ThemeProvider.Theme,
            newTheme: ThemeProvider.Theme
        ): CropMusic? {
            return playBack.onThemeChanged(oldTheme, newTheme)
        }

        fun increaseProgress() {
            sProgress++
        }

        fun setProgress(progress: Int) {
            Log.d("kimkakamusic", "setProgress" + progress + "sProgress" + sProgress)
            if (sProgress != progress) {
                sProgress = progress
                val whereTo = sProgress * 1000 / VideoConfig.getFps()
                playBack.seekTo(whereTo)
            }
        }

        fun removeMusicAtPosition(position: Int) {
            val whereTo = sProgress * 1000 / VideoConfig.getFps()
            Log.d("kimkakamusicr", "removeMusicAtPosition" + position)

            val cropMusic = playBack.removeAt(position, whereTo)
            Log.d("kimkakamusicr", "removeMusicAtPosition cropMusic" + cropMusic)
            if (cropMusic != null) {
                dataPreview.removeMusic(cropMusic)
            } else {
                dataPreview.synchronizedList(playBack.cropMusics)
            }
        }

        fun getSelectedMusic(): ArrayList<CropMusic> {
            return playBack.cropMusics
        }

        fun addCropMusic(cropMusic: CropMusic) {
            val currentDuration = sProgress * 1000 / VideoConfig.getFps()
            if (playBack.addCropMusic(this@MultiMusicPlayingActivity, cropMusic, currentDuration)) {
                Log.d("kimkakamusicr", "addCropMusic dataPreview" + cropMusic)
                dataPreview.addCropMusic(cropMusic)
            } else {
                Toast.makeText(
                    this@MultiMusicPlayingActivity,
                    R.string.msg_cant_play,
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }

        fun isPlaying() = !isPause

        fun getCurrentMusicPos(): Int = playBack.currentMusicPlayer

    }

}