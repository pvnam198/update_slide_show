package com.example.slide.framework.texttovideo

import java.io.File
import java.io.Serializable

class VideoTextExport(
    val url: String,
    val x: Int,
    val y: Int,
    val isFullTime: Boolean = true,
    val start: Int = 0,
    val end: Int = 0
) : Serializable {

    var isExits: Boolean = true

    fun checkExits() {
        isExits =  File(url).exists()
    }

    override fun toString(): String {
        return "VideoTextExport(url='$url', isExits=$isExits)"
    }


}