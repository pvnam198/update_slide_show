package com.example.slide.music_engine

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.slide.event.MusicSelectedChangeEvent
import com.example.slide.ui.video.video_preview.ThemeProvider
import com.example.slide.ui.video.video_preview.model.DataPreview
import org.greenrobot.eventbus.EventBus


class MultiMusicPlayBack(activity: Activity) : MusicSetupPlayBack(activity) {

    val cropMusics = ArrayList<CropMusic>()

    override fun initFromData(dataPreview: DataPreview, context: Context) {
        initMediaPlayers()
        dataPreview.cropMusics.forEach {
            currentMusicPlayer = 0
            try {
                mediaPlayers[count].prepare(activity, it)
                count++
                cropMusics.add(it)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        EventBus.getDefault().post(MusicSelectedChangeEvent())
    }

    override fun restoreMusicDefault(dataPreview: DataPreview, context: Context) {
        if (count == 0) currentMusicPlayer = 0
        try {
            mediaPlayers[count].prepare(activity, dataPreview.selectedTheme)
            val defaultMusic = CropMusic(defaultMusic = dataPreview.selectedTheme.defaultMusic)
            dataPreview.addCropMusic(defaultMusic)
            cropMusics.add(defaultMusic)
            count = 1
            EventBus.getDefault().post(MusicSelectedChangeEvent())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onThemeChanged(
        oldTheme: ThemeProvider.Theme,
        newTheme: ThemeProvider.Theme
    ): CropMusic? {
        Log.d("nampp", "onThemeChanged 0: ")
        if (shouldChangeMusic(oldTheme)) {
            Log.d("nampp", "onThemeChanged 1: ")
            release()
            initMediaPlayers()
            mediaPlayers[0].prepare(activity, newTheme)
            currentMusicPlayer = 0
            count = 1
            val cropMusic = CropMusic(newTheme.defaultMusic)
            cropMusics.add(cropMusic)
            EventBus.getDefault().post(MusicSelectedChangeEvent())
            return cropMusic
        } else {
            /*Log.d("nampp", "onThemeChanged 2: ")
            resetCurrentPos(sProgress)*/
        }
        return null
    }

    override fun addCropMusic(
        context: Context,
        cropMusic: CropMusic,
        currentDuration: Int
    ): Boolean {
        if (count == 0) currentMusicPlayer = 0
        val mediaPlayer = mediaPlayers[count]
        return try {
            seekTo(0)
            mediaPlayer.prepare(activity, cropMusic)
            count++
            cropMusics.add(cropMusic)
            seekTo(currentDuration)
            EventBus.getDefault().post(MusicSelectedChangeEvent())
            true
        } catch (e: Exception) {
            Log.d("kimkakamusicr", "Exception" + e.message)
            if (count == 0) currentMusicPlayer = -1
            false
        }
    }

    override fun removeAt(pos: Int, currentDuration: Int): CropMusic? {
        if (pos >= cropMusics.size) {
            EventBus.getDefault().post(MusicSelectedChangeEvent())
            return null
        } else {
            val cropMusic = cropMusics[pos]
            cropMusics.removeAt(pos)
            val isLastPlaying = isPlaying()
            Log.d("kimkakamusicr", "isLastPlaying" + isLastPlaying)
            removeMediaPlayerAt(pos)
            count--
            reSeekMusic(currentDuration, isLastPlaying)
            EventBus.getDefault().post(MusicSelectedChangeEvent())
            return cropMusic
        }
    }

    private fun reSeekMusic(currentDuration: Int, isLastPlaying: Boolean) {
        if (count <= 0) return
        var totalDuration = 0
        cropMusics.forEach { totalDuration += it.getDuration() }
        if (totalDuration == 0) return
        var whereTo = currentDuration % totalDuration
        var nextMediaPlayerPos = 0
        for (index in 0 until cropMusics.size) {
            val cropMusic = cropMusics[index]
            if (whereTo > cropMusic.getDuration()) {
                whereTo -= cropMusic.getDuration()
            } else {
                nextMediaPlayerPos = index
                break
            }
        }
        if (currentMusicPlayer != nextMediaPlayerPos) {
            if (currentMusicPlayer != -1) {
                mediaPlayers[currentMusicPlayer].pauseAndResetIfNeeded()
            }
            currentMusicPlayer = nextMediaPlayerPos
        }
        mediaPlayers[currentMusicPlayer].mediaPlayer.seekTo(whereTo)
        Log.d("kimkakamusicr", "reSeekMusic isLastPlaying" + isLastPlaying)
        Log.d("kimkakamusicr", "reSeekMusic isPlaying()" + isPlaying())
        if (isLastPlaying && !isPlaying()) start()
    }

    override fun seekFromPlayingDone() {
        if (currentMusicPlayer != -1) {
            mediaPlayers[currentMusicPlayer].resetAndPlay()
        }
    }

    override fun seekTo(where: Int) {
        if (count <= 0) return
        var totalDuration = 0
        cropMusics.forEach { totalDuration += it.getDuration() }
        if (totalDuration == 0) return
        var whereTo = where % totalDuration
        var nextMediaPlayerPos = 0
        for (index in 0 until cropMusics.size) {
            val cropMusic = cropMusics[index]
            if (whereTo > cropMusic.getDuration()) {
                whereTo -= cropMusic.getDuration()
            } else {
                nextMediaPlayerPos = index
                break
            }
        }
        if (currentMusicPlayer != nextMediaPlayerPos) {
            var wasPlaying = false
            if (currentMusicPlayer != -1) {
                wasPlaying = mediaPlayers[currentMusicPlayer].pauseAndResetIfNeeded()
            }
            currentMusicPlayer = nextMediaPlayerPos
            mediaPlayers[currentMusicPlayer].mediaPlayer.seekTo(whereTo)
            if (wasPlaying) start()
        } else {
            mediaPlayers[currentMusicPlayer].mediaPlayer.seekTo(whereTo)
        }
    }

    override fun start() {
        Log.d("kimkakamusic", "start" + currentMusicPlayer)
        if (currentMusicPlayer >= 0) {
            mediaPlayers[currentMusicPlayer].playIfNeeded()
        }
    }

    override fun pause() {
        Log.d("kimkakamusic", "pause" + currentMusicPlayer)
        if (currentMusicPlayer >= 0) {
            mediaPlayers[currentMusicPlayer].pauseIfNeeded()
        }
    }

    override fun stop() {
        Log.d("kimkakamusic", "stop" + currentMusicPlayer)
        if (currentMusicPlayer >= 0) {
            mediaPlayers[currentMusicPlayer].pauseIfNeeded()
            for (i in 0 until count) {
                mediaPlayers[i].mediaPlayer.seekTo(0)
            }
            currentMusicPlayer = 0
        }
    }

    override fun release() {
        Log.d("kimkakamusic", "release" + currentMusicPlayer)
        super.release()
        cropMusics.clear()
        EventBus.getDefault().post(MusicSelectedChangeEvent())
    }

    override fun isPlaying(): Boolean {
        if (currentMusicPlayer < 0) return false
        return mediaPlayers[currentMusicPlayer].isPlaying()
    }

    private fun shouldChangeMusic(oldTheme: ThemeProvider.Theme): Boolean {
        if (cropMusics.size > 1) return false
        if (cropMusics.size == 1) {
            if (cropMusics[0].defaultMusic != null) {
                return cropMusics[0].defaultMusic!!.id == oldTheme.defaultMusic.id
            } else return false
        }
        return true
    }
}