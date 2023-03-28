package com.example.slide.ui.edit_image

import android.graphics.Color
import com.example.slide.ui.edit_image.framework.AddTextProperties

object ShadowProvider {
    val SHADOW1 = AddTextProperties.TextShadow(0, 0, 0, Color.CYAN)

    val SHADOW2 = AddTextProperties.TextShadow(8, 4, 4, Color.parseColor("#FF1493"))

    val SHADOW3 = AddTextProperties.TextShadow(8, 4, 4, Color.MAGENTA)
    val SHADOW4 = AddTextProperties.TextShadow(8, 4, 4, Color.parseColor("#24ffff"))
    val SHADOW5 = AddTextProperties.TextShadow(8, 4, 4, Color.YELLOW)
    val SHADOW6 = AddTextProperties.TextShadow(8, 4, 4, Color.WHITE)
    val SHADOW7 = AddTextProperties.TextShadow(8, 4, 4, Color.GRAY)


    val SHADOW8 = AddTextProperties.TextShadow(8, -4, -4, Color.parseColor("#FF1493"))
    val SHADOW9 = AddTextProperties.TextShadow(8, -4, -4, Color.MAGENTA)
    val SHADOW10 = AddTextProperties.TextShadow(8, -4, -4, Color.parseColor("#24ffff"))
    val SHADOW11 = AddTextProperties.TextShadow(8, -4, -4, Color.YELLOW)
    val SHADOW12 = AddTextProperties.TextShadow(8, -4, -4, Color.WHITE)
    val SHADOW13 = AddTextProperties.TextShadow(8, -4, -4, Color.parseColor("#696969"))


    val SHADOW14 = AddTextProperties.TextShadow(8, -4, 4, Color.parseColor("#FF1493"))
    val SHADOW15 = AddTextProperties.TextShadow(8, -4, 4, Color.MAGENTA)
    val SHADOW16 = AddTextProperties.TextShadow(8, -4, 4, Color.parseColor("#24ffff"))
    val SHADOW17 = AddTextProperties.TextShadow(8, -4, 4, Color.YELLOW)
    val SHADOW18 = AddTextProperties.TextShadow(8, -4, 4, Color.WHITE)
    val SHADOW19 = AddTextProperties.TextShadow(8, -4, 4, Color.parseColor("#696969"))

    val SHADOW20 = AddTextProperties.TextShadow(8, 4, -4, Color.parseColor("#FF1493"))
    val SHADOW21 = AddTextProperties.TextShadow(8, 4, -4, Color.MAGENTA)
    val SHADOW22 = AddTextProperties.TextShadow(8, 4, -4, Color.parseColor("#24ffff"))
    val SHADOW23 = AddTextProperties.TextShadow(8, 4, -4, Color.YELLOW)
    val SHADOW24 = AddTextProperties.TextShadow(8, 4, -4, Color.WHITE)
    val SHADOW25 = AddTextProperties.TextShadow(8, 4, -4, Color.parseColor("#696969"))

    val SHADOWS = arrayListOf(
        SHADOW1, SHADOW2, SHADOW3, SHADOW4, SHADOW5,
        SHADOW6, SHADOW7, SHADOW8, SHADOW9, SHADOW10,
        SHADOW11, SHADOW12, SHADOW13, SHADOW14, SHADOW15,
        SHADOW16, SHADOW17, SHADOW18, SHADOW19, SHADOW20,
        SHADOW21, SHADOW22, SHADOW23, SHADOW24, SHADOW25,
    )
}