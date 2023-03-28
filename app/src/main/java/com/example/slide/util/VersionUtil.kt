package com.example.slide.util

import android.os.Build

object VersionUtil {

    fun isAndroid6OrLess(): Boolean {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.M
    }

    fun isAndroid7OrLess(): Boolean {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1
    }

    fun isAndroid8OrLess(): Boolean {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.O
    }

}