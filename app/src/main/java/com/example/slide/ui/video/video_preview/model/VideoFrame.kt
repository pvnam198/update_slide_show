package com.example.slide.ui.video.video_preview.model

import android.net.Uri
import java.io.Serializable

data class VideoFrame(val id: Int, val url: String, val nameRes: Int) : Serializable {
    fun getUri(): Uri {
        return Uri.parse("file:///android_asset/frame/$url")
    }
}