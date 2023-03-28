package com.example.slide.videolib

import com.example.slide.MyApplication

object VideoConfig {

    const val VIDEO_QUALITY = "video_quality"

    const val VIDEO_QUALITY_480 = 0

    const val VIDEO_QUALITY_720 = 1

    const val VIDEO_QUALITY_1080 = 2

    const val VIDEO_WIDTH_1080 = 1920

    const val VIDEO_HEIGHT_1080 = 1080

    const val VIDEO_WIDTH_720 = 1280

    const val VIDEO_HEIGHT_720 = 720

    const val VIDEO_WIDTH_480 = 720

    const val VIDEO_HEIGHT_480 = 480

    private var fps = 18

    private var secondPerImageAnimate = 2

    var animatedFrame = 2 * fps - 8 * fps / 15

    var animatedFrameSub = animatedFrame - 1

    private var videoHeight = 480

    private var videoWidth = 720

    fun getFps(): Int = fps

    fun setFps(fps: Int) {
        this.fps = fps
    }

    fun getSecondPerImageAnimate(): Int = secondPerImageAnimate

    fun setSecondPerImageAnimate(
        secondPerImageAnimate: Int,
        myApplication: MyApplication
    ): Boolean {
        val isChangedAnimatedFrame = secondPerImageAnimate == 1 || this.secondPerImageAnimate == 1
        this.secondPerImageAnimate = secondPerImageAnimate
        if (isChangedAnimatedFrame) {
            animatedFrame = if (secondPerImageAnimate == 1) {
                fps - 4 * fps / 15
            } else {
                2 * fps - 8 * fps / 15
            }
            animatedFrameSub = animatedFrame - 1
        } else {
            myApplication.videoDataState.updateOutputWhenSecondChanged()
        }
        return isChangedAnimatedFrame
    }

    fun initSecondPerImageAnimate(secondPerImageAnimate: Int) {
        animatedFrame = if (secondPerImageAnimate == 1) {
            fps - 4 * fps / 15
        } else {
            2 * fps - 8 * fps / 15
        }
        animatedFrameSub = animatedFrame - 1
        this.secondPerImageAnimate = secondPerImageAnimate
    }

    fun setupVideoQuality(videoQuality: Int) {
        when (videoQuality) {
            VIDEO_QUALITY_480 -> {
                videoHeight = VIDEO_HEIGHT_480
                videoWidth = VIDEO_WIDTH_480
            }
            VIDEO_QUALITY_720 -> {
                videoHeight = VIDEO_HEIGHT_720
                videoWidth = VIDEO_WIDTH_720
            }
            VIDEO_QUALITY_1080 -> {
                videoHeight = VIDEO_HEIGHT_1080
                videoWidth = VIDEO_WIDTH_1080
            }
            else -> {
                videoHeight = VIDEO_HEIGHT_480
                videoWidth = VIDEO_WIDTH_480
            }
        }
    }

    fun getVideoWidth(): Int {
        return videoWidth
    }

    fun getVideoHeight(): Int {
        return videoHeight
    }
}