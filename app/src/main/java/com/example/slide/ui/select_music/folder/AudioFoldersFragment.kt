package com.example.slide.ui.select_music.folder

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.FragmentAudioFoldersBinding
import com.example.slide.ui.select_music.event.SongLoadedEvent
import com.example.slide.ui.select_music.event.SongLoadingEvent
import com.example.slide.ui.select_music.model.Folder
import com.example.slide.ui.select_music.search.SearchFragment
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AudioFoldersFragment : BaseFragment<FragmentAudioFoldersBinding>() {
    override fun bindingView(): FragmentAudioFoldersBinding {
        return FragmentAudioFoldersBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({ R.layout.fragment_audio_folders },{true})

    var adapter: FolderAdapter? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoading(event: SongLoadingEvent) {
        initialState()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoaded(event: SongLoadedEvent) {
        initialState()
    }

    override fun initConfiguration() {
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvFolders.layoutManager = layoutManager
        adapter = FolderAdapter(this)
        binding.rvFolders.adapter = adapter

        initialState()
    }

    override fun initListener() {
        super.initListener()
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnSearch.setOnClickListener { parentFragmentManager.beginTransaction().replace(R.id.root_view, SearchFragment()).addToBackStack(null).commit() }
    }

    private fun initialState() {
    }

    override fun releaseData() {
    }

    fun gotoFolder(folder: Folder) {
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.content, FolderTrackFragment.getInstance(folder))
            .addToBackStack(null).commit()
    }
}