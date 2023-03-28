package com.example.slide.music_engine

import android.app.Activity
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.PowerManager
import com.example.slide.event.MusicSelectedChangeEvent
import org.greenrobot.eventbus.EventBus

abstract class MusicSetupPlayBack(val activity: Activity) : Playback,
    MediaPlayer.OnCompletionListener {

    companion object {
        const val MAX_MUSIC_COUNT = 10
    }

    val mediaPlayers = ArrayList<MyMediaPlayer>()

    override var currentMusicPlayer = -1

    var count = 0

    fun initMediaPlayers() {
        mediaPlayers.clear()
        for (i in 1..MAX_MUSIC_COUNT) {
            val mediaPlayer = createNewMyMediaPlayer()
            mediaPlayers.add(mediaPlayer)
        }
        currentMusicPlayer = -1
        count = 0
    }

    override fun release() {
        currentMusicPlayer = -1
        mediaPlayers.forEach {
            it.release()
        }
    }

    fun removeMediaPlayerAt(pos: Int) {
        mediaPlayers[pos].release()
        mediaPlayers.removeAt(pos)
        mediaPlayers.add(createNewMyMediaPlayer())
        if (currentMusicPlayer >= pos) {
            currentMusicPlayer--
        }
    }

    private fun createNewMyMediaPlayer(): MyMediaPlayer {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setWakeMode(
            activity,
            PowerManager.PARTIAL_WAKE_LOCK
        )
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        return MyMediaPlayer(mediaPlayer)
    }

    override fun onCompletion(mediaPlayer: MediaPlayer?) {
        currentMusicPlayer = (currentMusicPlayer + 1) % count
        mediaPlayers[currentMusicPlayer].mediaPlayer.start()
        EventBus.getDefault().post(MusicSelectedChangeEvent())
    }
}