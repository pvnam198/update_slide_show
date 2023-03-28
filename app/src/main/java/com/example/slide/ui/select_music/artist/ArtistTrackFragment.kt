package com.example.slide.ui.select_music.artist

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.ui.select_music.SelectMusicActivity
import com.example.slide.ui.select_music.SubTrackAdapter
import com.example.slide.ui.select_music.event.SongLoadedEvent
import com.example.slide.ui.select_music.event.SongLoadingEvent
import com.example.slide.ui.select_music.model.Artist
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider
import kotlinx.android.synthetic.main.fragment_audio_artist_track.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ArtistTrackFragment : BaseFragment() {

    override fun initViewTools() = InitViewTools({ R.layout.fragment_audio_artist_track }, { true })

    companion object {

        private const val EXTRA_ARTIST = "extra_artist"

        fun getInstance(artist: Artist): ArtistTrackFragment {
            val fragment = ArtistTrackFragment()
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_ARTIST, artist)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var artist: Artist? = null

    override fun extractData(bundle: Bundle?) {
        bundle ?: return
        artist = bundle.getSerializable(EXTRA_ARTIST) as Artist?
        super.extractData(bundle)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(EXTRA_ARTIST, artist)
    }

    var adapter: SubTrackAdapter? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoading(event: SongLoadingEvent) {
        initState()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoaded(event: SongLoadedEvent) {
        initState()
        artist ?: return
        adapter?.updateData(LocalMusicProvider.getInstance().getSongsByArtist(artist!!.name))
    }

    override fun initConfiguration() {
        super.initConfiguration()
        initState()
        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        trackRecycleView.layoutManager = layoutManager
        trackRecycleView.setHasFixedSize(true)
        adapter = SubTrackAdapter(requireActivity() as SelectMusicActivity)
        trackRecycleView.adapter = adapter
        artist?.let {
            if (LocalMusicProvider.getInstance().isSongLoaded)
                adapter?.updateData(LocalMusicProvider.getInstance().getSongsByArtist(it.name))

            if (it.art.isNotEmpty())
                Glide.with(this).load(it.art).placeholder(R.drawable.ic_audio_artists)
                    .into(iv_artist)
            tv_title.text = it.name
            tv_song_count.text = getString(R.string.song_number_format, it.songNumber)
        }
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun initState() {
        if (LocalMusicProvider.getInstance().state == LocalMusicProvider.LOADING) {
            progress.visibility = View.VISIBLE
            trackRecycleView.visibility = View.INVISIBLE
            noSongLayout.visibility = View.GONE
        } else {
            if (LocalMusicProvider.getInstance().state == LocalMusicProvider.LOADED && LocalMusicProvider.getInstance().albums.size == 0) {
                progress.visibility = View.GONE
                trackRecycleView.visibility = View.INVISIBLE
                noSongLayout.visibility = View.VISIBLE
            } else {
                progress.visibility = View.GONE
                trackRecycleView.visibility = View.VISIBLE
                noSongLayout.visibility = View.GONE
            }

        }
    }

    override fun releaseData() {
    }

}