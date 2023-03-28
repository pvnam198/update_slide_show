package com.example.slide.music_engine

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.util.Log
import com.example.slide.ui.video.video_preview.ThemeProvider

class MyMediaPlayer(val mediaPlayer: MediaPlayer, var isInitialed: Boolean = false) {

    fun release() {
        mediaPlayer.release()
        isInitialed = false
    }

    fun prepare(context: Context, cropMusic: CropMusic) {
        mediaPlayer.reset()
        var descriptor : AssetFileDescriptor? = null
        if (cropMusic.defaultMusic != null) {
            val defaultMusic = cropMusic.defaultMusic
            descriptor = context.resources.assets.openFd(defaultMusic.url)
            mediaPlayer.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
        } else if (cropMusic.track != null)
            mediaPlayer.setDataSource(cropMusic.track.url)
        mediaPlayer.prepare()
        isInitialed = true
        descriptor?.close()
    }

    fun prepare(context: Context, theme: ThemeProvider.Theme) {
        mediaPlayer.reset()
        val descriptor: AssetFileDescriptor = context.resources.assets.openFd(theme.defaultMusic.url)
        mediaPlayer.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
        mediaPlayer.prepare()
        isInitialed = true
        descriptor.close()
    }

    fun pauseIfNeeded(): Boolean {
        if(isInitialed && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            return true
        }
        return false
    }

    fun pauseAndResetIfNeeded(): Boolean {
        if(isInitialed) {
            mediaPlayer.seekTo(0)
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                return true
            }
        }
        return false
    }

    fun resetAndPlay() {
        if(isInitialed) {
            mediaPlayer.seekTo(0)
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
            }
        }
    }

    fun playIfNeeded() {
        Log.d("kimkakamusic", "playIfNeeded1" )
        if (isInitialed && !mediaPlayer.isPlaying) {
            Log.d("kimkakamusic", "playIfNeeded2" )
            mediaPlayer.start()
        }
    }

    fun isPlaying(): Boolean {
        if(!isInitialed) return false
        return mediaPlayer.isPlaying
    }
}