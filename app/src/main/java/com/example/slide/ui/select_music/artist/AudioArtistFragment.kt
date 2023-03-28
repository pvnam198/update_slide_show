package com.example.slide.ui.select_music.artist

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.ui.select_music.event.SongLoadedEvent
import com.example.slide.ui.select_music.event.SongLoadingEvent
import com.example.slide.ui.select_music.model.Artist
import com.example.slide.ui.select_music.search.SearchFragment
import kotlinx.android.synthetic.main.fragment_audio_artist.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AudioArtistFragment : BaseFragment() {

    override fun initViewTools() = InitViewTools({ R.layout.fragment_audio_artist },{true})

    override fun releaseData() {
    }

    private var adapter: ArtistAdapter? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoading(event: SongLoadingEvent) {
        initialState()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoaded(event: SongLoadedEvent) {
        initialState()
        adapter?.notifyDataSetChanged()
    }

    override fun initConfiguration() {
        adapter = ArtistAdapter(this)
        rv_artist.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rv_artist.adapter = adapter
        initialState()
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener { requireActivity().onBackPressed() }
        btn_search.setOnClickListener { parentFragmentManager.beginTransaction().replace(R.id.root_view, SearchFragment()).addToBackStack(null).commit() }
    }

    private fun initialState() {
    }

    fun gotoArtist(artist: Artist) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.root_view, ArtistTrackFragment.getInstance(artist))
            .addToBackStack(null)
            .commit()
    }
}