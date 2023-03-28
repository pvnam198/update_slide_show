package com.example.slide.ui.select_music.files

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.ui.select_music.SelectMusicActivity
import com.example.slide.ui.select_music.event.SongLoadedEvent
import com.example.slide.ui.select_music.event.SongLoadingEvent
import com.example.slide.ui.select_music.model.MyFile
import com.example.slide.ui.select_music.search.SearchFragment
import com.example.slide.util.MusicUtils
import com.example.slide.util.MyStatic
import com.example.slide.util.Utils
import kotlinx.android.synthetic.main.fragment_audio_files.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList

class AudioFilesFragment : BaseFragment() {

    override fun initViewTools() = InitViewTools({ R.layout.fragment_audio_files },{true})

    var tabContentIndex = 0

    var currentPath = ""

    var adapter: FileAdapter? = null

    var headerAdapter: FileHeaderAdapter? = null

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
        rv_files.layoutManager = layoutManager
        val layoutManager2 = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rv_files_header.layoutManager = layoutManager2
        adapter = FileAdapter(this)
        rv_files.adapter = adapter
        headerAdapter = FileHeaderAdapter(this)
        rv_files_header.adapter = headerAdapter
        showSongsForFolder(MyStatic.EXTERNAL_STORAGE_PATH)
        initialState()
    }

    fun showSongsForFolder(path: String) {
        currentPath = path
        val files = ArrayList<MyFile>()
        files.addAll(Utils.getListFileFromPath(requireContext(), path))
        headerAdapter?.updateData(Utils.getListFileHeaderFromPath(path))
        headerAdapter?.let {
            if (it.itemCount > 0)
                rv_files_header.scrollToPosition(it.itemCount - 1)

        }
        if (path == MyStatic.EXTERNAL_STORAGE_PATH) {
            tabContentIndex = 0
        } else {
            tv_title.text = path.substring(path.lastIndexOf("/") + 1, path.length)
            tabContentIndex = 1
        }
        if (files.isEmpty()) {
            layout_no_folder.visibility = View.VISIBLE
        } else {
            layout_no_folder.visibility = View.GONE
        }
        adapter?.updateData(files)
    }

    fun onFileClick(file: MyFile) {
        val track = MusicUtils.getTrackFromPath(requireContext(),file.url)
        (requireActivity() as SelectMusicActivity).trimMusic(track)
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener { requireActivity().onBackPressed() }
        btn_search.setOnClickListener { parentFragmentManager.beginTransaction().replace(R.id.root_view, SearchFragment()).addToBackStack(null).commit() }
    }

    override fun releaseData() {

    }

    fun onBackPressed() {
        if (tabContentIndex == 0) {
            (requireActivity() as SelectMusicActivity).superBackPressed()
            return
        }
        var previousPath = MyStatic.EXTERNAL_STORAGE_PATH
        if (currentPath != MyStatic.EXTERNAL_STORAGE_PATH) {
            previousPath = currentPath.substring(0, currentPath.lastIndexOf("/"))
        }
        showSongsForFolder(previousPath)
    }

    private fun initialState() {
    }
}