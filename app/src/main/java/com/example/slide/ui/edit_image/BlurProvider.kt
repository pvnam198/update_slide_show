package com.example.slide.ui.edit_image

import com.example.slide.R

object BlurProvider {

    private val BLUR_1_MASK =
        BlurMask("blur/blur_1_mask.png", "blur/blur_1_shadow.png", R.drawable.blur_1)
    private val BLUR_2_MASK =
        BlurMask( "blur/blur_2_mask.png", "blur/blur_2_shadow.png", R.drawable.blur_2)
    private val BLUR_3_MASK =
        BlurMask( "blur/blur_3_mask.png", "blur/blur_3_shadow.png", R.drawable.blur_3)
    private val BLUR_4_MASK =
        BlurMask( "blur/blur_4_mask.png", "blur/blur_4_shadow.png", R.drawable.blur_4)
    private val BLUR_5_MASK =
        BlurMask( "blur/blur_5_mask.png", "blur/blur_5_shadow.png", R.drawable.blur_5)
    private val BLUR_6_MASK =
        BlurMask( "blur/blur_6_mask.png", "blur/blur_6_shadow.png", R.drawable.blur_6)
    private val BLUR_7_MASK =
        BlurMask( "blur/blur_7_mask.png", "blur/blur_7_shadow.png", R.drawable.blur_7)
    private val BLUR_8_MASK =
        BlurMask( "blur/blur_8_mask.png", "blur/blur_8_shadow.png", R.drawable.blur_8)
    private val BLUR_9_MASK =
        BlurMask( "blur/blur_9_mask.png", "blur/blur_9_shadow.png", R.drawable.blur_9)

    val BLURS = arrayOf(
        BLUR_1_MASK,
        BLUR_2_MASK,
        BLUR_3_MASK,
        BLUR_4_MASK,
        BLUR_5_MASK,
        BLUR_6_MASK,
        BLUR_7_MASK,
        BLUR_8_MASK,
        BLUR_9_MASK
    )

    class BlurMask(val maskAsset: String, val shadowAsset: String, val imageRes: Int)

}