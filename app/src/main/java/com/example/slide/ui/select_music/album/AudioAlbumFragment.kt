package com.example.slide.ui.select_music.album

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.ui.select_music.event.SongLoadedEvent
import com.example.slide.ui.select_music.event.SongLoadingEvent
import com.example.slide.ui.select_music.model.MusicAlbum
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider
import com.example.slide.ui.select_music.search.SearchFragment
import kotlinx.android.synthetic.main.fragment_audio_album.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AudioAlbumFragment : BaseFragment() {

    override fun initViewTools() = InitViewTools({ R.layout.fragment_audio_album },{true})

    override fun releaseData() {
    }

    var adapter: AlbumAdapter? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoading(event: SongLoadingEvent) {
        intialState()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoaded(event: SongLoadedEvent) {
        intialState()
        adapter?.notifyDataSetChanged()
    }

    override fun initConfiguration() {
        intialState()

        val layoutManager = GridLayoutManager(requireContext(), 2)
        albumRecycleView.setLayoutManager(layoutManager)
        albumRecycleView.setHasFixedSize(true)
        adapter = AlbumAdapter(this)
        albumRecycleView.setAdapter(adapter)
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener { requireActivity().onBackPressed() }
        btn_search.setOnClickListener { parentFragmentManager.beginTransaction().replace(R.id.root_view, SearchFragment()).addToBackStack(null).commit() }
    }
    private fun intialState() {
        if (LocalMusicProvider.getInstance().state == LocalMusicProvider.LOADING) {
            progress.visibility = View.VISIBLE
            albumRecycleView.visibility = View.INVISIBLE
            noAlbumLayout.visibility = View.GONE
        } else {
            if (LocalMusicProvider.getInstance().state == LocalMusicProvider.LOADED && LocalMusicProvider.getInstance().albums.size == 0) {
                progress.visibility = View.GONE
                albumRecycleView.visibility = View.INVISIBLE
                noAlbumLayout.visibility = View.VISIBLE
            } else {
                progress.visibility = View.GONE
                albumRecycleView.visibility = View.VISIBLE
                noAlbumLayout.visibility = View.GONE
            }
        }
    }


    fun goToAlbum(album: MusicAlbum) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.content, AlbumTrackFragment.getInstance(album)).addToBackStack(null)
            .commit()
    }
}