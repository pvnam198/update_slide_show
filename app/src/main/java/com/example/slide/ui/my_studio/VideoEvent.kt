package com.example.slide.ui.my_studio

sealed class VideoEvent
data class VideoDeletedEvent(private val video: MyVideo): VideoEvent()
