package com.example.slide.music_engine

import java.io.Serializable

data class DefaultMusic(val id: Int, val nameRes: Int, val url: String,val duration: Int = 0) : Serializable