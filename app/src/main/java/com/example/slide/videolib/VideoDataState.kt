package com.example.slide.videolib

import com.example.slide.AnimateImage
import com.example.slide.event.VideoProgressStateChanged
import org.greenrobot.eventbus.EventBus

class VideoDataState {

    companion object {

        const val STATE_NONE = 0

        const val STATE_CREATE_IMAGE = 1

        const val STATE_CREATE_VIDEO = 2

    }

    private val outputAnimateImages = ArrayList<AnimateImage>()

    private var totalAnimateImageFrame: Int = 1

    private var totalImage: Int = 1

    var totalImageFrame: Int = 1

    var totalSecond = 0

    var state: Int = STATE_NONE

    private var isPreparingImage480 = false

    private var isPreparingImage720 = false

    private var isPreparingImage1080 = false

    var isExportingVideo = false

    var progress: Int = 0

    var isDonePrepareImages480 = false

    var isDonePrepareImages720 = false

    var isDonePrepareImages1080 = false

    val outputImages = ArrayList<String>()

    var isCancel = false

    fun goSaveVideoMode() {
        state = STATE_CREATE_VIDEO
        progress = 0
        EventBus.getDefault().post(VideoProgressStateChanged(progress))
    }

    fun updateProgress(fProgress: Float) {
        var newProgress = fProgress.toInt()
        if (newProgress < 0) newProgress = 0
        if (newProgress > 100) newProgress = 100
        if (newProgress != progress) {
            progress = newProgress
            EventBus.getDefault().post(VideoProgressStateChanged(progress))
        }
    }

    @Synchronized
    fun addImage(image: String, pos: Int) {
        val animateImage = AnimateImage(image, pos)
        outputAnimateImages.add(animateImage)
        val staticFrame =
            VideoConfig.getSecondPerImageAnimate() * VideoConfig.getFps() - VideoConfig.animatedFrame
        when (pos) {
            AnimateImage.FIRST -> {
                val step = staticFrame / 2
                for (i in 0..step) {
                    outputImages.add(image)
                }
            }
            AnimateImage.LAST -> {
                val step = staticFrame / 2 + staticFrame % 2
                for (i in 0..step) {
                    outputImages.add(image)
                }
            }
            AnimateImage.NORMAL -> {

                outputImages.add(image)
            }
        }
        val newProgress = outputAnimateImages.size * 100 / totalAnimateImageFrame
        if (newProgress != progress) {
            progress = newProgress
            EventBus.getDefault().post(VideoProgressStateChanged(progress))
        }
    }

    @Synchronized
    fun updateOutputWhenSecondChanged() {
        totalImageFrame = (totalImage - 1) * VideoConfig.animatedFrame * VideoConfig.getFps()
        outputImages.clear()
        outputAnimateImages.forEachIndexed { index, animateImage ->
            val staticFrame =
                VideoConfig.getSecondPerImageAnimate() * VideoConfig.getFps() - VideoConfig.animatedFrame
            when (animateImage.pos) {
                AnimateImage.FIRST -> {
                    val step = staticFrame / 2
                    for (i in 0..step) {
                        outputImages.add(animateImage.url)
                    }
                }
                AnimateImage.LAST -> {
                    val step = staticFrame / 2 + staticFrame % 2
                    for (i in 0..step) {
                        outputImages.add(animateImage.url)
                    }
                }
                AnimateImage.NORMAL -> {

                    outputImages.add(animateImage.url)
                }
            }
        }
    }

    fun setSaveVideoProgress(newProgress: Int) {
        if (newProgress != progress) {
            progress = newProgress
            EventBus.getDefault().post(VideoProgressStateChanged(progress))
        }
    }

    fun resetDoneState() {
        isDonePrepareImages480 = false
        isDonePrepareImages720 = false
        isDonePrepareImages1080 = false
    }

    fun initWithImageNumber(totalImage: Int) {
        isDonePrepareImages480 = false
        isDonePrepareImages720 = false
        isDonePrepareImages1080 = false
        outputImages.clear()
        outputAnimateImages.clear()
        progress = 0
        state = STATE_CREATE_IMAGE
        isCancel = false
        initBaseWithImageNumber(totalImage)
    }

    fun initBaseWithImageNumber(totalImage: Int) {
        this.totalImage = totalImage
        totalAnimateImageFrame = calculateAnimateImageFrame(totalImage)
        totalSecond = calculateDurationWithImages(totalImage)
        totalImageFrame = calculateImageFrameWithImages(totalImage)
    }

    fun calculateDurationWithImages(totalImage: Int): Int {
        return (totalImage - 1) * VideoConfig.getSecondPerImageAnimate()
    }

    fun calculateImageFrameWithImages(totalImage: Int): Int {
        return (totalImage - 1) * VideoConfig.getSecondPerImageAnimate() * VideoConfig.getFps()
    }

    fun calculateAnimateImageFrame(totalImage: Int): Int {
        return (totalImage - 1) * VideoConfig.animatedFrame
    }

    fun isPreparingDone(videoQuality: Int): Boolean {
        return when (videoQuality) {
            VideoConfig.VIDEO_QUALITY_480 -> {
                isDonePrepareImages480
            }
            VideoConfig.VIDEO_QUALITY_720 -> {
                isDonePrepareImages720
            }
            else -> {
                isDonePrepareImages1080
            }
        }
    }

    fun isPreparing(videoQuality: Int): Boolean {
        return when (videoQuality) {
            VideoConfig.VIDEO_QUALITY_480 -> {
                isPreparingImage480
            }
            VideoConfig.VIDEO_QUALITY_720 -> {
                isPreparingImage720
            }
            else -> {
                isPreparingImage1080
            }
        }
    }

    fun setIsPreparing(isPrepare: Boolean, videoQuality: Int) {
        when (videoQuality) {
            VideoConfig.VIDEO_QUALITY_480 -> {
                isPreparingImage480 = isPrepare
            }
            VideoConfig.VIDEO_QUALITY_720 -> {
                isPreparingImage720 = isPrepare
            }
            else -> {
                isPreparingImage1080 = isPrepare
            }
        }
    }
}