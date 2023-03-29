package com.example.slide.ui.select_music.folder

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.FragmentAudioSubTrackBinding
import com.example.slide.ui.select_music.SelectMusicActivity
import com.example.slide.ui.select_music.SubTrackAdapter
import com.example.slide.ui.select_music.SubTrackContainer
import com.example.slide.ui.select_music.event.SongLoadedEvent
import com.example.slide.ui.select_music.event.SongLoadingEvent
import com.example.slide.ui.select_music.model.Folder
import com.example.slide.ui.select_music.model.Track
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FolderTrackFragment : BaseFragment<FragmentAudioSubTrackBinding>(), SubTrackContainer {
    override fun bindingView(): FragmentAudioSubTrackBinding {
        return FragmentAudioSubTrackBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({ R.layout.fragment_audio_sub_track }, { true })

    override fun releaseData() {
    }

    var adapter: SubTrackAdapter? = null

    companion object {

        private const val EXTRA_FOLDER = "extra_folder"

        fun getInstance(folder: Folder): FolderTrackFragment {
            val fragment = FolderTrackFragment()
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_FOLDER, folder)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var folder: Folder? = null

    override fun extractData(bundle: Bundle?) {
        folder = bundle?.getSerializable(EXTRA_FOLDER) as Folder?
        super.extractData(bundle)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(EXTRA_FOLDER, folder)
    }

    override fun initConfiguration() {
        super.initConfiguration()
        initState()
        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.trackRecycleView.layoutManager = layoutManager
        binding.trackRecycleView.setHasFixedSize(true)
        adapter = SubTrackAdapter(requireActivity() as SelectMusicActivity)
        binding.trackRecycleView.adapter = adapter
        folder ?: return
        binding.tvTitle.text = folder!!.name
        if (LocalMusicProvider.instance.isSongLoaded) adapter?.updateData(
            LocalMusicProvider.getInstance().getSongsByFolder(folder!!.url)
        )
    }

    override fun initListener() {
        super.initListener()

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoading(event: SongLoadingEvent) {
        initState()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoaded(event: SongLoadedEvent) {
        initState()
        folder ?: return
        adapter?.updateData(LocalMusicProvider.getInstance().getSongsByFolder(folder!!.url))
    }

    private fun initState() {
        if (LocalMusicProvider.getInstance().state == LocalMusicProvider.LOADING) {
            binding.progress.visibility = View.VISIBLE
            binding.trackRecycleView.visibility = View.INVISIBLE
            binding.noSongLayout.visibility = View.GONE
        } else {
            if (LocalMusicProvider.getInstance().state == LocalMusicProvider.LOADED && LocalMusicProvider.getInstance().albums.size == 0) {
                binding.progress.visibility = View.GONE
                binding.trackRecycleView.visibility = View.INVISIBLE
                binding.noSongLayout.visibility = View.VISIBLE
            } else {
                binding.progress.visibility = View.GONE
                binding.trackRecycleView.visibility = View.VISIBLE
                binding.noSongLayout.visibility = View.GONE
            }
        }
    }

    override fun onClicked(track: Track) {
    }

    override fun getCustomContext(): Context {
        return requireContext()
    }
}