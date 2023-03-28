package com.example.slide.util

import com.example.slide.videolib.VideoConfig
import java.util.*

object StringUtils {
    fun getDurationDisplayFromMillis(millis: Int): String {
        val time = millis / 1000
        val ss = time % 60
        val mm = time / 60
        return "" + (mm / 10) + "" + mm % 10 + ":" + ss / 10 + "" + ss % 10
    }

    fun getDurationDisplayFromMillis(millis: Long): String {
        val time = millis / 1000
        val ss = time % 60
        var mm = time / 60
        if (mm < 60)
            return String.format(Locale.US,"%02d:%02d", mm, ss)
        val hh = mm / 60
        mm %= 60
        return String.format(Locale.US,"%d:%02d:%02d", hh, mm, ss)
    }

    fun getDurationDisplayFromSeconds(seconds: Int): String {
        val ss = seconds % 60
        val mm = seconds / 60
        if (mm < 60)
            return String.format(Locale.US,"%02d:%02d", mm, ss)
        return String.format(Locale.US,"%d:%02d", mm, ss)
    }

    fun getDurationDisplayFromFrame(frame: Int): String {
        val j = frame / VideoConfig.getFps()
        val mm = j / 60
        val ss = j % 60
        return String.format(Locale.US,"%02d:%02d", mm, ss)
    }
}