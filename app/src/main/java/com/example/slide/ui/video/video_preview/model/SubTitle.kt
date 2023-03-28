package com.example.slide.ui.video.video_preview.model

import java.io.Serializable

class SubTitle(
    var text: String,
    var startTime: Int,
    var endTime: Int,
    var imagePath: String,
    val startX: Int = 0,
    val startY: Int = 0,
    var isFullTime: Boolean = true,
    var id: Long = 0
) : Serializable {
    init {
        id = System.currentTimeMillis()
    }
}