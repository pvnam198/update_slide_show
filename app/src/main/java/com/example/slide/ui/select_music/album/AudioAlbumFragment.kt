package com.example.slide.ui.select_music.album

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.FragmentAudioAlbumBinding
import com.example.slide.ui.select_music.event.SongLoadedEvent
import com.example.slide.ui.select_music.event.SongLoadingEvent
import com.example.slide.ui.select_music.model.MusicAlbum
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider
import com.example.slide.ui.select_music.search.SearchFragment
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AudioAlbumFragment : BaseFragment<FragmentAudioAlbumBinding>() {
    override fun bindingView(): FragmentAudioAlbumBinding {
        return FragmentAudioAlbumBinding.inflate(layoutInflater)
    }

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
        binding.albumRecycleView.setLayoutManager(layoutManager)
        binding.albumRecycleView.setHasFixedSize(true)
        adapter = AlbumAdapter(this)
        binding.albumRecycleView.setAdapter(adapter)
    }

    override fun initListener() {
        super.initListener()
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnSearch.setOnClickListener { parentFragmentManager.beginTransaction().replace(R.id.root_view, SearchFragment()).addToBackStack(null).commit() }
    }
    private fun intialState() {
        if (LocalMusicProvider.getInstance().state == LocalMusicProvider.LOADING) {
            binding.progress.visibility = View.VISIBLE
            binding.albumRecycleView.visibility = View.INVISIBLE
            binding.noAlbumLayout.visibility = View.GONE
        } else {
            if (LocalMusicProvider.getInstance().state == LocalMusicProvider.LOADED && LocalMusicProvider.getInstance().albums.size == 0) {
                binding.progress.visibility = View.GONE
                binding.albumRecycleView.visibility = View.INVISIBLE
                binding.noAlbumLayout.visibility = View.VISIBLE
            } else {
                binding.progress.visibility = View.GONE
                binding.albumRecycleView.visibility = View.VISIBLE
                binding.noAlbumLayout.visibility = View.GONE
            }
        }
    }


    fun goToAlbum(album: MusicAlbum) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.content, AlbumTrackFragment.getInstance(album)).addToBackStack(null)
            .commit()
    }
}