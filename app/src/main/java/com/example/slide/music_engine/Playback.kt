package com.example.slide.music_engine

import android.content.Context
import com.example.slide.ui.video.video_preview.ThemeProvider
import com.example.slide.ui.video.video_preview.model.DataPreview

interface Playback {

    var currentMusicPlayer: Int

    fun initFromData(dataPreview: DataPreview, context: Context)

    fun onThemeChanged(oldTheme: ThemeProvider.Theme, newTheme: ThemeProvider.Theme): CropMusic?

    fun addCropMusic(
        context: Context, cropMusic: CropMusic, currentDuration: Int
    ): Boolean

    fun restoreMusicDefault(dataPreview: DataPreview, context: Context)

    fun removeAt(pos: Int, currentDuration: Int): CropMusic?

    fun seekTo(where: Int)

    fun seekFromPlayingDone()

    fun start()

    fun pause()

    fun stop()

    fun release()

    fun isPlaying(): Boolean

}