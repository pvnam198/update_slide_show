package com.example.slide.ui.video.video_preview.model

import android.util.Log
import com.example.slide.framework.texttovideo.VideoTextExport
import com.example.slide.model.Image
import com.example.slide.music_engine.CropMusic
import com.example.slide.ui.video.video_preview.ThemeProvider
import java.io.Serializable

class DataPreview(
    var images: ArrayList<Image>,
    var texts: ArrayList<VideoTextExport> = ArrayList(),
    var videoFrame: VideoFrame? = null,
    var selectedTheme: ThemeProvider.Theme = ThemeProvider.SHUFFLE_EFFECT
) : Serializable {

    val cropMusics = ArrayList<CropMusic>()

    companion object {
        private const val serialVersionUID: Long = 6524565098264567690L
    }

    fun removeMusic(cropMusic: CropMusic) {
        cropMusics.remove(cropMusic)
        Log.d("kimkakamusicr", "cropMusics remove" + cropMusics.size)
    }

    fun addCropMusic(cropMusic: CropMusic) {
        cropMusics.add(cropMusic)
        Log.d("kimkakamusicr", "cropMusics add" + cropMusics.size)
    }

    fun synchronizedList(cropMusics: ArrayList<CropMusic>) {
        this.cropMusics.clear()
        cropMusics.forEach {
            this.cropMusics.add(it)
        }
    }

    fun synchronizedListFromTheme(cropMusic: CropMusic) {
        this.cropMusics.clear()
        cropMusics.add(cropMusic)
    }
}