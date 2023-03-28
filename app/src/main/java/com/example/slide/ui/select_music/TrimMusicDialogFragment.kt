package com.example.slide.ui.select_music

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.example.slide.R
import com.example.slide.framework.cutter.CheapSoundFile
import com.example.slide.framework.cutter.myrangeseekbar.AudioCutterView
import com.example.slide.ui.select_music.model.Track
import com.example.slide.util.FileUtils
import com.example.slide.util.StringUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_trim_music.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*


class TrimMusicDialogFragment : BottomSheetDialogFragment(), MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val uiScope = CoroutineScope(Dispatchers.Main)

    private var cuttingMusicJob: Job? = null

    var isBind = false

    companion object {

        const val TAG = "TrimMusicDialogFragment"

        const val EXTRA_TRACK = "track"

        fun getInstance(track: Track): TrimMusicDialogFragment {
            val fragment = TrimMusicDialogFragment()
            val arguments = Bundle()
            arguments.putSerializable(EXTRA_TRACK, track)
            fragment.arguments = arguments
            return fragment
        }
    }

    private var start = 0

    private var end = 0

    private var duration = 0

    private var mediaPlayer: MediaPlayer? = null

    var track: Track? = null

    private var timer: Timer? = null

    private var behavior: BottomSheetBehavior<View>? = null

    private var callback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    override fun onSaveInstanceState(outState: Bundle) {
        track?.let { track ->
            outState.putSerializable(EXTRA_TRACK, track)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_trim_music, container, false)
    }

    var isViewVisible = false

    override fun onStart() {
        super.onStart()
        isViewVisible = true
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    override fun onStop() {
        super.onStop()
        isViewVisible = false
        pauseMusic()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isBind = true
        track = (savedInstanceState ?: arguments)!!.getSerializable(EXTRA_TRACK) as Track
        initMediaPlayer()
        initView()
        initListener()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val bottomSheet =
                    (it as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            behavior?.addBottomSheetCallback(callback)
        }
        return dialog
    }

    private fun initView() {
        track?.let { track ->
            val duration = track.duration / 1000
            seek_bar.count = duration
            seek_bar.maxProgress = duration
            tv_song_name.text = track.title
            start = 0
            end = duration
            this.duration = duration
            tv_start_time.text = StringUtils.getDurationDisplayFromSeconds(start)
            tv_end_time.text = StringUtils.getDurationDisplayFromSeconds(end)
            tv_time_center.text = StringUtils.getDurationDisplayFromSeconds(end)
        }
    }

    private fun saveTrackToStorage(track: Track): Track {
        val title = FileUtils.convertNormalFullFileName(track.url)
        val fileInput = FileInputStream(track.url)
        val folder = FileUtils.getMusicDefaultDir(requireContext())
        val outFile = File(folder, title)
        val out = FileOutputStream(outFile)
        val buffer = ByteArray(1024)
        var read: Int
        while (fileInput.read(buffer).also { read = it } != -1) {
            out.write(buffer, 0, read)
        }
        val newTrack = Track(System.currentTimeMillis().toInt(), outFile.absolutePath)
        newTrack.title = title
        newTrack.duration = track.duration
        return newTrack
    }

    private fun initListener() {
        btn_play.setOnClickListener {
            togglePlayButton()
        }

        btn_check.setOnClickListener {
            track?.let {

                pauseMusic()

                if (start == 0 && end == it.duration / 1000) {
                    btn_progress_bar.visibility = View.VISIBLE
                    cuttingMusicJob = saveMusic(it)
                    cuttingMusicJob?.start()
                } else {
                    btn_progress_bar.visibility = View.VISIBLE
                    isCancelable = false
                    cuttingMusicJob = trimMusic(it, start, end)
                    cuttingMusicJob?.start()
                }
            }
        }

        seek_bar.setOnValueChangedListener(object : AudioCutterView.OnValueChangedListener() {
            override fun onValueChanged(progress: Int, fromUser: Boolean) {
                if (fromUser)
                    mediaPlayer?.seekTo(progress * 1000)
            }

            override fun onStartChanged(minProgress: Int, fromUser: Boolean) {
                super.onStartChanged(minProgress, fromUser)
                if (fromUser) {
                    start = minProgress
                    tv_start_time.text = StringUtils.getDurationDisplayFromSeconds(start)
                    tv_time_center.text = StringUtils.getDurationDisplayFromSeconds(end - start)
                    if (position() < minProgress * 1000) {
                        mediaPlayer?.seekTo(minProgress * 1000)
                        seek_bar?.progress = minProgress
                    }
                }
            }

            override fun onEndChanged(maxProgress: Int, fromUser: Boolean) {
                super.onEndChanged(maxProgress, fromUser)
                if (fromUser) {
                    end = maxProgress
                    tv_end_time.text = StringUtils.getDurationDisplayFromSeconds(end)
                    tv_time_center.text = StringUtils.getDurationDisplayFromSeconds(end - start)
                    if (position() > maxProgress * 1000) {
                        mediaPlayer?.seekTo(maxProgress * 1000)
                        seek_bar?.progress = maxProgress
                    }
                }
            }

        })

        btn_decrease_start.setOnClickListener {
            if (start > 0) {
                start--
                tv_start_time.text = StringUtils.getDurationDisplayFromSeconds(start)
                seek_bar.minProgress = start
            }
        }
        btn_increase_start.setOnClickListener {
            if (start < end - 5) {
                start++
                tv_start_time.text = StringUtils.getDurationDisplayFromSeconds(start)
                seek_bar.minProgress = start
            }
        }

        btn_decrease_end.setOnClickListener {
            if (end > start + 5) {
                end--
                tv_end_time.text = StringUtils.getDurationDisplayFromSeconds(end)
                seek_bar.maxProgress = end
            }
        }
        btn_increase_end.setOnClickListener {
            if (end < duration) {
                end++
                tv_end_time.text = StringUtils.getDurationDisplayFromSeconds(end)
                seek_bar.maxProgress = end
            }
        }

    }

    private fun initMediaPlayer() {
        track?.let { track ->
            mediaPlayer = MediaPlayer()
            mediaPlayer?.let {
                it.setWakeMode(
                        context,
                        PowerManager.PARTIAL_WAKE_LOCK
                )
                it.setAudioAttributes(
                        AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                )
                it.setOnPreparedListener(this)
                it.setOnCompletionListener(this)
                it.setOnSeekCompleteListener(this)
                it.reset()
                try {
                    it.setDataSource(track.url)
                    it.prepare()
                    iv_toggle_play.setImageResource(R.drawable.ic_play)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }

            timer = Timer()
            timer!!.scheduleAtFixedRate(MyTimerTask(), 300, 300)
        }
    }


    override fun onDestroyView() {
        isBind = false
        seek_bar?.setOnValueChangedListener(null)
        timer?.cancel()
        timer?.purge()
        timer = null
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        cuttingMusicJob?.cancel()
        behavior?.removeBottomSheetCallback(callback)
    }

    private fun pauseMusic() {
        if (isPlaying()) {
            mediaPlayer?.pause()
            initPlayingState()
        }
    }

    fun startMusic() {
        if (!isPlaying()) {
            mediaPlayer?.start()
            initPlayingState()
        }
    }

    private fun togglePlayButton() {
        if (isPlaying()) {
            mediaPlayer?.pause()
        } else {
            mediaPlayer?.start()
        }
        initPlayingState()
    }

    private fun initPlayingState() {
        if (isPlaying()) {
            iv_toggle_play.setImageResource(R.drawable.ic_pause)
        } else {
            iv_toggle_play.setImageResource(R.drawable.ic_play)
        }
    }

    fun isPlaying(): Boolean {
        return mediaPlayer != null && mediaPlayer!!.isPlaying
    }

    fun position(): Int {
        return try {
            if (mediaPlayer == null)
                0
            else
                mediaPlayer!!.currentPosition
        } catch (e: IllegalStateException) {
            0
        }
    }

    override fun onSeekComplete(mp: MediaPlayer?) {

    }

    override fun onCompletion(mp: MediaPlayer?) {
        initPlayingState()
    }

    override fun onPrepared(mp: MediaPlayer?) {

    }

    private fun trimMusic(track: Track, start: Int, end: Int) = ioScope.launch {
        val fileDir = FileUtils.getUniqueAudioTrimmedFileUrl(requireContext(), track.url, (activity as SelectMusicActivity).draft.id)
        Log.d("kimkakacutter", fileDir)
        try {
            val musicFile = File(fileDir)
            val cheapSoundFile = CheapSoundFile.create(track.url, null)
            if (cheapSoundFile == null) {
                Toast.makeText(
                        requireContext(),
                        getString(R.string.music_file_error),
                        Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            } else {
                val sampleRate = cheapSoundFile.sampleRate
                val samplesPerFrame = cheapSoundFile.samplesPerFrame
                val frameStart = (1.0 * start * sampleRate / samplesPerFrame + 0.5).toInt()
                val frameEnd = (1.0 * end * sampleRate / samplesPerFrame + 0.5).toInt()
                cheapSoundFile.WriteFile(musicFile, frameStart, frameEnd - frameStart)
            }
            CoroutineScope(Dispatchers.Main).launch {
                if (isBind) {
                    btn_progress_bar?.visibility = View.INVISIBLE
                    val newTrack = Track()
                    newTrack.title = musicFile.name
                    newTrack.url = musicFile.absolutePath
                    newTrack.duration = (end - start) * 1000
                    (requireActivity() as SelectMusicActivity).trimMMusicDone(newTrack)
                }
            }
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(requireContext(), R.string.msg_cant_cut_audio, Toast.LENGTH_LONG)
                        .show()
                (requireActivity() as SelectMusicActivity).trimMMusicDone(track)
            }
        }
    }

    private fun saveMusic(track: Track) = ioScope.launch {
        val fileDir = FileUtils.getUniqueAudioTrimmedFileUrl(requireContext(), track.url, (activity as SelectMusicActivity).draft.id)
        Log.d("kimkakacutter", fileDir)
        try {
            val newTrack = saveTrackToStorage(track)
            CoroutineScope(Dispatchers.Main).launch {
                if (isBind) {
                    btn_progress_bar?.visibility = View.INVISIBLE
                    (requireActivity() as SelectMusicActivity).trimMMusicDone(newTrack)
                }
            }
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(requireContext(), R.string.msg_cant_cut_audio, Toast.LENGTH_LONG)
                        .show()
                (requireActivity() as SelectMusicActivity).trimMMusicDone(track)
            }
        }
    }

    inner class MyTimerTask : TimerTask() {
        override fun run() {
            if (isPlaying() && isViewVisible)
                uiScope.launch {
                    seek_bar?.progress = position() / 1000
                }
        }
    }


}