package com.example.slide.util

import android.graphics.Typeface


object FontProvider {

    private const val NONE = ""

    private const val A_HUNDRED_MILES_FONT_PATH = "fonts/ahundredmiles.ttf"

    private const val FREE_UNIVERSAL_BOLD_FONT_PATH = "fonts/FreeUniversal-Bold.ttf"

    private const val GOOD_DOG_FONT_PATH = "fonts/GoodDog.otf"

    private const val JESTER_FONT_PATH = "fonts/Jester.ttf"

    private const val JUNCTION_FONT_PATH = "fonts/Junction 02.otf"

    private const val LAINE_FONT_PATH = "fonts/Laine.ttf"

    private const val NEON_FONT_PATH = "fonts/NEON.TTF"

    private const val OSP_DIN_FONT_PATH = "fonts/OSP-DIN.ttf"

    private const val PACIFICO_FONT_PATH = "fonts/Pacifico.ttf"

    private const val POLSKU_FONT_PATH = "fonts/Polsku.ttf"

    private const val SANS_THIRTEEN_BLACK_PATH = "fonts/SansThirteenBlack.ttf"

    private const val SILENT_REACTION_PATH = "fonts/Silent Reaction.ttf"

    private const val SOFIA_REGULAR_PATH = "fonts/Sofia-Regular.ttf"

    private const val SWEET_SENSATIONS_PATH = "fonts/SweetSensations.ttf"

    private const val VIN_QUE_PATH = "fonts/vinque.ttf"

    private const val WALTOGRAPHUI_PATH = "fonts/waltographUI.ttf"

    private val defaultFont: Typeface = Typeface.DEFAULT

    fun fonts(): ArrayList<String> {
        return arrayListOf(
            NONE,
            A_HUNDRED_MILES_FONT_PATH,
            FREE_UNIVERSAL_BOLD_FONT_PATH,
            GOOD_DOG_FONT_PATH,
            JESTER_FONT_PATH,
            JUNCTION_FONT_PATH,
            LAINE_FONT_PATH,
            NEON_FONT_PATH,
            OSP_DIN_FONT_PATH,
            PACIFICO_FONT_PATH,
            POLSKU_FONT_PATH,
            SANS_THIRTEEN_BLACK_PATH,
            SILENT_REACTION_PATH,
            SOFIA_REGULAR_PATH,
            SWEET_SENSATIONS_PATH,
            VIN_QUE_PATH,
            WALTOGRAPHUI_PATH
        )
    }

    fun getDefaultFont(): Typeface {
        return defaultFont
    }
}