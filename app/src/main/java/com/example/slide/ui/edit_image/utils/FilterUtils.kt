package com.example.slide.ui.edit_image.utils

import android.graphics.Bitmap
import android.util.Log
import org.wysaid.common.SharedContext
import org.wysaid.nativePort.CGEImageHandler
import java.text.MessageFormat
import java.util.*

object FilterUtils {
    class FilterBean internal constructor(var config: String, var name: String)

    val OVERLAY_CONFIG = arrayListOf(
        FilterBean("", ""),
        FilterBean("#unpack @krblend sr overlay/1.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/2.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/3.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/4.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/5.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/6.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/7.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/8.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/9.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/10.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/11.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/12.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/13.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/14.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/15.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/16.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/17.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/18.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/19.jpg 100", ""),
        FilterBean("#unpack @krblend sr overlay/20.jpg 100", ""),
    )

    fun getLstBitmapWithOverlay(bitmap: Bitmap?): List<Bitmap> {
        val lstBitmaps: MutableList<Bitmap> = ArrayList()
        val glContext = SharedContext.create()
        glContext.makeCurrent()
        val handler = CGEImageHandler()
        handler.initWithBitmap(bitmap)
        for (config in OVERLAY_CONFIG) {
            handler.setFilterWithConfig(config.config)
            handler.processFilters()
            val bmp = handler.resultBitmap
            lstBitmaps.add(bmp)
        }
        handler.release()
        glContext.release()
        return lstBitmaps
    }

    fun getBlurImageFromBitmap(bitmap: Bitmap?, intensity: Float): Bitmap? {
        val glContext = SharedContext.create()
        glContext.makeCurrent()
        val handler = CGEImageHandler()
        val arguments = intensity / 10.0f
        handler.initWithBitmap(bitmap)
        handler.setFilterWithConfig(
            MessageFormat.format(
                "@blur lerp {0}",
                "$arguments"
            )
        )
        handler.processFilters()
        val bmp = handler.resultBitmap
        handler.release()
        glContext.release()
        return bmp
    }

    fun getBlackAndWhiteImageFromBitmap(bitmap: Bitmap?): Bitmap? {
        val glContext = SharedContext.create()
        glContext.makeCurrent()
        val handler = CGEImageHandler()
        handler.initWithBitmap(bitmap)
        handler.setFilterWithConfig("@adjust saturation 0")
        handler.processFilters()
        val bmp = handler.resultBitmap
        handler.release()
        glContext.release()
        return bmp
    }
}