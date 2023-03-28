package com.example.slide.framework.texttovideo

import android.content.Context
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.example.slide.R
import com.example.slide.ui.edit_image.framework.StickerView

object StickIconProvider {
    fun getDeleteIconIcon(context: Context): BitmapActionIcon {
        val deleteIcon = BitmapActionIcon(
            ContextCompat.getDrawable(context, R.drawable.sticker_ic_close_white_18dp),
            BitmapActionIcon.LEFT_TOP, BitmapActionIcon.REMOVE
        )
        deleteIcon.iconEvent = object : ActionEvent {
            override fun onActionDown(stickerView: TextListToVideoView, event: MotionEvent) {
            }

            override fun onActionMove(stickerView: TextListToVideoView, event: MotionEvent) {
            }

            override fun onActionUp(stickerView: TextListToVideoView, event: MotionEvent) {
                stickerView.removeCurrentSticker()
            }

        }
        return deleteIcon
    }

    fun getZoomIcon(context: Context): BitmapActionIcon {
        val zoomIcon = BitmapActionIcon(
            ContextCompat.getDrawable(context, R.drawable.sticker_ic_scale_white_18dp),
            BitmapActionIcon.RIGHT_BOTOM, BitmapActionIcon.ZOOM
        )
        zoomIcon.iconEvent = object : ActionEvent {
            override fun onActionDown(stickerView: TextListToVideoView, event: MotionEvent) {
            }

            override fun onActionMove(stickerView: TextListToVideoView, event: MotionEvent) {
                stickerView.zoomAndRotateCurrentSticker(event)
            }

            override fun onActionUp(stickerView: TextListToVideoView, event: MotionEvent) {
                stickerView.onStickerOperationListener?.onStickerZoomFinished(stickerView.currentSticker!!)
            }

        }
        return zoomIcon
    }

    fun getFlipIcon(context: Context): BitmapActionIcon {
        val flipIcon = BitmapActionIcon(
            ContextCompat.getDrawable(context, R.drawable.sticker_ic_flip_white_18dp),
            BitmapActionIcon.LEFT_BOTTOM, BitmapActionIcon.FLIP
        )
        flipIcon.iconEvent = object : ActionEvent {
            override fun onActionDown(stickerView: TextListToVideoView, event: MotionEvent) {
            }

            override fun onActionMove(stickerView: TextListToVideoView, event: MotionEvent) {
            }

            override fun onActionUp(stickerView: TextListToVideoView, event: MotionEvent) {
                stickerView.flipCurrentSticker(StickerView.FLIP_HORIZONTALLY)
            }

        }
        return flipIcon
    }

    fun getEditIcon(context: Context): BitmapActionIcon {
        val editIcon = BitmapActionIcon(
            ContextCompat.getDrawable(context, R.drawable.ic_edt),
            BitmapActionIcon.RIGHT_TOP, BitmapActionIcon.EDIT
        )
        editIcon.iconEvent = object : ActionEvent {
            override fun onActionDown(stickerView: TextListToVideoView, event: MotionEvent) {
            }

            override fun onActionMove(stickerView: TextListToVideoView, event: MotionEvent) {
            }

            override fun onActionUp(stickerView: TextListToVideoView, event: MotionEvent) {
                stickerView.editTextSticker()
            }

        }
        return editIcon
    }
}