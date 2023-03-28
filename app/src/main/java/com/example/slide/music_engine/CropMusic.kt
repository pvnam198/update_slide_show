package com.example.slide.music_engine

import com.example.slide.ui.select_music.model.Track
import java.io.Serializable

class CropMusic(val defaultMusic: DefaultMusic? = null, val track: Track? = null) : Serializable {
    fun getDuration(): Int = defaultMusic?.duration ?: track!!.duration
    override fun toString(): String {
        return "CropMusic(defaultMusic=$defaultMusic, track=$track)"
    }


}