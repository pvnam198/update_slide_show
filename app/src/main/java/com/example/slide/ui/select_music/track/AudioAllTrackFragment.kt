package com.example.slide.ui.select_music.track

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.ui.select_music.SelectMusicActivity
import com.example.slide.ui.select_music.event.SongLoadedEvent
import com.example.slide.ui.select_music.event.SongLoadingEvent
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider
import com.example.slide.ui.select_music.search.SearchFragment
import kotlinx.android.synthetic.main.fragment_audio_all_track.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AudioAllTrackFragment : BaseFragment() {

    override fun initViewTools() = InitViewTools({ R.layout.fragment_audio_all_track }, { true })

    override fun releaseData() {
    }

    companion object {
        private const val TAG = "AudioAllTrackFragment"
    }

    var adapter: TrackAdapter? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoading(event: SongLoadingEvent) {
        initialState()
        fast_scroll_recycler_view.setFastScrollEnabled(false)
        fast_scroll_recycler_view.setHideScrollbar(true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoaded(event: SongLoadedEvent) {
        initialState()
        adapter?.let {
            it.notifyDataSetChanged()
            val size = it.itemCount
            if (size == 0) {
                noSongLayout.visibility =
                    View.VISIBLE
                fast_scroll_recycler_view.setFastScrollEnabled(false)
                fast_scroll_recycler_view.setHideScrollbar(true)
            }
            if (size <= 12) {
                fast_scroll_recycler_view.setFastScrollEnabled(false)
                fast_scroll_recycler_view.setHideScrollbar(true)
            }
        }
    }

    override fun initConfiguration() {
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        fast_scroll_recycler_view.layoutManager = layoutManager
        adapter = TrackAdapter(requireActivity() as SelectMusicActivity)
        fast_scroll_recycler_view.adapter = adapter
        val size = adapter!!.itemCount
        if (size == 0) {
            fast_scroll_recycler_view.setFastScrollEnabled(false)
            fast_scroll_recycler_view.setHideScrollbar(true)
            noSongLayout.visibility = View.VISIBLE
        }
        if (size < 12) {
            fast_scroll_recycler_view.setFastScrollEnabled(false)
            fast_scroll_recycler_view.setHideScrollbar(true)
        }
        initialState()
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener { requireActivity().onBackPressed() }
        btn_search.setOnClickListener {
            parentFragmentManager.beginTransaction().add(R.id.root_view, SearchFragment())
                .addToBackStack(null).commit()
        }
    }

    private fun initialState() {
        if (LocalMusicProvider.getInstance().state == LocalMusicProvider.LOADING) {
            progress.visibility = View.VISIBLE
            fast_scroll_recycler_view.visibility = View.INVISIBLE
            noSongLayout.visibility = View.GONE
        } else {
            if (LocalMusicProvider.getInstance().state == LocalMusicProvider.LOADED && LocalMusicProvider.getInstance().albums.size == 0) {
                progress.visibility = View.GONE
                fast_scroll_recycler_view.visibility = View.INVISIBLE
                noSongLayout.visibility = View.VISIBLE
            } else {
                progress.visibility = View.GONE
                fast_scroll_recycler_view.visibility = View.VISIBLE
                noSongLayout.visibility = View.GONE
            }
        }
    }
}