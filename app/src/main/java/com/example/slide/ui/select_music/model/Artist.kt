package com.example.slide.ui.select_music.model

import java.io.Serializable

class Artist(var name: String, var songNumber: Int, var art: String) : Serializable {

    @Synchronized
    fun increaseSongNumber() {
        songNumber++
    }

}