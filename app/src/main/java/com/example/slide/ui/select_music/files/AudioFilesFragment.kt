package com.example.slide.ui.select_music.files

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.FragmentAudioFilesBinding
import com.example.slide.ui.select_music.SelectMusicActivity
import com.example.slide.ui.select_music.event.SongLoadedEvent
import com.example.slide.ui.select_music.event.SongLoadingEvent
import com.example.slide.ui.select_music.model.MyFile
import com.example.slide.ui.select_music.search.SearchFragment
import com.example.slide.util.MusicUtils
import com.example.slide.util.MyStatic
import com.example.slide.util.Utils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList

class AudioFilesFragment : BaseFragment<FragmentAudioFilesBinding>() {
    override fun bindingView(): FragmentAudioFilesBinding {
        return FragmentAudioFilesBinding.inflate(layoutInflater)
    }

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
        binding.rvFiles.layoutManager = layoutManager
        val layoutManager2 = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvFilesHeader.layoutManager = layoutManager2
        adapter = FileAdapter(this)
        binding.rvFiles.adapter = adapter
        headerAdapter = FileHeaderAdapter(this)
        binding.rvFilesHeader.adapter = headerAdapter
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
                binding.rvFilesHeader.scrollToPosition(it.itemCount - 1)

        }
        if (path == MyStatic.EXTERNAL_STORAGE_PATH) {
            tabContentIndex = 0
        } else {
            binding.tvTitle.text = path.substring(path.lastIndexOf("/") + 1, path.length)
            tabContentIndex = 1
        }
        if (files.isEmpty()) {
            binding.layoutNoFolder.visibility = View.VISIBLE
        } else {
            binding.layoutNoFolder.visibility = View.GONE
        }
        adapter?.updateData(files)
    }

    fun onFileClick(file: MyFile) {
        val track = MusicUtils.getTrackFromPath(requireContext(),file.url)
        (requireActivity() as SelectMusicActivity).trimMusic(track)
    }

    override fun initListener() {
        super.initListener()
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnSearch.setOnClickListener { parentFragmentManager.beginTransaction().replace(R.id.root_view, SearchFragment()).addToBackStack(null).commit() }
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