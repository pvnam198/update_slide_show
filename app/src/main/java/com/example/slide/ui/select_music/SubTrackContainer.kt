package com.example.slide.ui.select_music

import android.content.Context
import com.example.slide.ui.select_music.model.Track

interface SubTrackContainer {

    fun onClicked(track: Track)

    fun getCustomContext(): Context
}