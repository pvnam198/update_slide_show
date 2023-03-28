package com.example.slide.ui.select_music.folder

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.ui.select_music.event.SongLoadedEvent
import com.example.slide.ui.select_music.event.SongLoadingEvent
import com.example.slide.ui.select_music.model.Folder
import com.example.slide.ui.select_music.search.SearchFragment
import kotlinx.android.synthetic.main.fragment_audio_folders.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AudioFoldersFragment : BaseFragment() {

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
        rv_folders.layoutManager = layoutManager
        adapter = FolderAdapter(this)
        rv_folders.adapter = adapter

        initialState()
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener { requireActivity().onBackPressed() }
        btn_search.setOnClickListener { parentFragmentManager.beginTransaction().replace(R.id.root_view, SearchFragment()).addToBackStack(null).commit() }
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