package com.example.slide.ui.select_music

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.slide.R
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.example.slide.database.entities.Draft
import com.example.slide.music_engine.DefaultMusic
import com.example.slide.ui.select_music.album.AudioAlbumFragment
import com.example.slide.ui.select_music.artist.AudioArtistFragment
import com.example.slide.ui.select_music.default.DefaultMusicFragment
import com.example.slide.ui.select_music.files.AudioFilesFragment
import com.example.slide.ui.select_music.folder.AudioFoldersFragment
import com.example.slide.ui.select_music.model.Track
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider
import com.example.slide.ui.select_music.track.AudioAllTrackFragment
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import kotlinx.android.synthetic.main.activity_select_music.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SelectMusicActivity : BaseActivity() {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private var loadingMusicJob: Job? = null

    private lateinit var track: Track

    private var fileFragment: AudioFilesFragment? = null

    private var trimMusicDialogFragment: TrimMusicDialogFragment? = null

    lateinit var draft: Draft

    override fun initViewTools() = InitViewTools({ R.layout.activity_select_music })

    companion object {

        private const val TAG = "SelectMusicActivity"

        private const val EXTRA_DRAFT = "EXTRA_DRAFT"

        fun getInstance(context: Context, draft: Draft): Intent {
            return Intent(context, SelectMusicActivity::class.java).apply {
                putExtra(EXTRA_DRAFT, draft)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(EXTRA_DRAFT, draft)
    }

    override fun initConfiguration(savedInstanceState: Bundle?) {
        super.initConfiguration(savedInstanceState)
    }

    override fun extractData(bundle: Bundle) {
        super.extractData(bundle)
        draft = intent.getParcelableExtra<Draft>(EXTRA_DRAFT) as Draft
    }

    override fun releaseData() {
        loadingMusicJob?.cancel()
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener {
            onBackPressed()
        }
        btn_all_tracks.setOnClickListener {
            fileFragment = null
            supportFragmentManager.beginTransaction().replace(R.id.content, AudioAllTrackFragment())
                    .addToBackStack(null).commit()
        }
        btn_artists.setOnClickListener {
            fileFragment = null
            supportFragmentManager.beginTransaction().replace(R.id.content, AudioArtistFragment())
                    .addToBackStack(null).commit()
        }
        btn_album.setOnClickListener {
            fileFragment = null
            supportFragmentManager.beginTransaction().replace(R.id.content, AudioAlbumFragment())
                    .addToBackStack(null).commit()
        }
        btn_folders.setOnClickListener {
            fileFragment = null
            supportFragmentManager.beginTransaction().replace(R.id.content, AudioFoldersFragment())
                    .addToBackStack(null).commit()
        }
        btn_files.setOnClickListener {
            fileFragment = AudioFilesFragment()
            supportFragmentManager.beginTransaction().replace(R.id.content, fileFragment!!)
                    .addToBackStack(null).commit()
        }
        btn_default_music.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.content, DefaultMusicFragment())
                    .addToBackStack(null).commit()
        }
    }

    override fun onBackPressed() {
        if (fileFragment == null) {
            super.onBackPressed()
        } else {
            fileFragment?.onBackPressed()
        }
    }

    fun superBackPressed() {
        super.onBackPressed()
        fileFragment = null
    }

    fun trimMusic(track: Track) {
        this.track = track

        if (track.duration <= 5000) {
            trimMMusicDone(track)
        } else {
            showBottomSheet(track)
        }
    }

    fun trimMMusicDone(track: Track) {
        setResult(Activity.RESULT_OK, VideoCreateActivity.getIntent(track))
        finish()
    }

    fun onDefaultMusicSelected(defaultMusic: DefaultMusic) {
        setResult(Activity.RESULT_OK, VideoCreateActivity.getIntent(defaultMusic))
        finish()
    }

    private fun showBottomSheet(track: Track) {
        trimMusicDialogFragment = TrimMusicDialogFragment.getInstance(track)
        trimMusicDialogFragment!!.show(supportFragmentManager, TAG)
    }

    override fun initTask() {
        super.initTask()
        loadingMusicJob = loadSong()
    }

    private fun loadSong() = ioScope.launch {
        LocalMusicProvider.getInstance().init()
    }

    fun onTrimTrack(track: Track, start: Int, end: Int) {
        setResult(Activity.RESULT_OK, VideoCreateActivity.getIntent(track))
    }

}