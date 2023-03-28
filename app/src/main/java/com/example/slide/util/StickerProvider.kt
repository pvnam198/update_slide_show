package com.example.slide.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.slide.R
import com.example.slide.ui.edit_image.events.*
import com.example.slide.ui.edit_image.framework.BitmapFloatingItemIcon

object StickerProvider {
    fun getTextStickerIcons(context: Context): List<BitmapFloatingItemIcon> {
        val deleteIcon =
            BitmapFloatingItemIcon(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.sticker_ic_close_white_18dp
                ),
                BitmapFloatingItemIcon.LEFT_TOP,
                BitmapFloatingItemIcon.REMOVE
            )
        deleteIcon.iconEvent = DeleteIconEvent()

        val zoomIcon =
            BitmapFloatingItemIcon(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.sticker_ic_scale_white_18dp
                ),
                BitmapFloatingItemIcon.RIGHT_BOTOM,
                BitmapFloatingItemIcon.ZOOM
            )
        zoomIcon.iconEvent = ZoomIconEvent()

        val flipIcon =
            BitmapFloatingItemIcon(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.sticker_ic_flip_white_18dp
                ),
                BitmapFloatingItemIcon.RIGHT_TOP,
                BitmapFloatingItemIcon.FLIP
            )
        flipIcon.iconEvent = FlipHorizontallyEvent()

        val rotateIcon =
            BitmapFloatingItemIcon(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_rt
                ),
                BitmapFloatingItemIcon.RIGHT_BOTOM,
                BitmapFloatingItemIcon.ROTATE
            )
        rotateIcon.iconEvent = ZoomIconEvent()

        val editIcon =
            BitmapFloatingItemIcon(
                ContextCompat.getDrawable(context, R.drawable.ic_edt),
                BitmapFloatingItemIcon.RIGHT_TOP,
                BitmapFloatingItemIcon.EDIT
            )
        editIcon.iconEvent = EditTextIconEvent()

        val centerIcon =
            BitmapFloatingItemIcon(
                ContextCompat.getDrawable(context, R.drawable.icon_center),
                BitmapFloatingItemIcon.LEFT_BOTTOM,
                BitmapFloatingItemIcon.ALIGN_HORIZONTALLY
            )
        centerIcon.iconEvent = AlignHorizontallyEvent()

        return listOf(
            deleteIcon,
            zoomIcon,
            flipIcon,
            rotateIcon,
            editIcon,
            centerIcon
        )
    }
}