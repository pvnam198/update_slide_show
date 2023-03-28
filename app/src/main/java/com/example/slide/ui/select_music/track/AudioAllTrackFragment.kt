package com.example.slide.ui.select_music.track

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.FragmentAudioAllTrackBinding
import com.example.slide.ui.select_music.SelectMusicActivity
import com.example.slide.ui.select_music.event.SongLoadedEvent
import com.example.slide.ui.select_music.event.SongLoadingEvent
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider
import com.example.slide.ui.select_music.search.SearchFragment
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AudioAllTrackFragment : BaseFragment<FragmentAudioAllTrackBinding>() {
    override fun bindingView(): FragmentAudioAllTrackBinding {
        return FragmentAudioAllTrackBinding.inflate(layoutInflater)
    }

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
        binding.fastScrollRecyclerView.setFastScrollEnabled(false)
        binding.fastScrollRecyclerView.setHideScrollbar(true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoaded(event: SongLoadedEvent) {
        initialState()
        adapter?.let {
            it.notifyDataSetChanged()
            val size = it.itemCount
            if (size == 0) {
                binding.noSongLayout.visibility =
                    View.VISIBLE
                binding.fastScrollRecyclerView.setFastScrollEnabled(false)
                binding.fastScrollRecyclerView.setHideScrollbar(true)
            }
            if (size <= 12) {
                binding.fastScrollRecyclerView.setFastScrollEnabled(false)
                binding.fastScrollRecyclerView.setHideScrollbar(true)
            }
        }
    }

    override fun initConfiguration() {
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.fastScrollRecyclerView.layoutManager = layoutManager
        adapter = TrackAdapter(requireActivity() as SelectMusicActivity)
        binding.fastScrollRecyclerView.adapter = adapter
        val size = adapter!!.itemCount
        if (size == 0) {
            binding.fastScrollRecyclerView.setFastScrollEnabled(false)
            binding.fastScrollRecyclerView.setHideScrollbar(true)
            binding.noSongLayout.visibility = View.VISIBLE
        }
        if (size < 12) {
            binding.fastScrollRecyclerView.setFastScrollEnabled(false)
            binding.fastScrollRecyclerView.setHideScrollbar(true)
        }
        initialState()
    }

    override fun initListener() {
        super.initListener()
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnSearch.setOnClickListener {
            parentFragmentManager.beginTransaction().add(R.id.root_view, SearchFragment())
                .addToBackStack(null).commit()
        }
    }

    private fun initialState() {
        if (LocalMusicProvider.getInstance().state == LocalMusicProvider.LOADING) {
            binding.progress.visibility = View.VISIBLE
            binding.fastScrollRecyclerView.visibility = View.INVISIBLE
            binding.noSongLayout.visibility = View.GONE
        } else {
            if (LocalMusicProvider.getInstance().state == LocalMusicProvider.LOADED && LocalMusicProvider.getInstance().albums.size == 0) {
                binding.progress.visibility = View.GONE
                binding.fastScrollRecyclerView.visibility = View.INVISIBLE
                binding.noSongLayout.visibility = View.VISIBLE
            } else {
                binding.progress.visibility = View.GONE
                binding.fastScrollRecyclerView.visibility = View.VISIBLE
                binding.noSongLayout.visibility = View.GONE
            }
        }
    }
}