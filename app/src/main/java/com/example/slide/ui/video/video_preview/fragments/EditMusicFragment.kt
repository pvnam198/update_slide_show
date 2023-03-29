package com.example.slide.ui.video.video_preview.fragments

import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.common.Common
import com.example.slide.databinding.FragmentEditVideoMusicBinding
import com.example.slide.event.MusicSelectedChangeEvent
import com.example.slide.event.MusicStateChangedEvent
import com.example.slide.music_engine.MusicSetupPlayBack
import com.example.slide.ui.select_music.SelectMusicActivity
import com.example.slide.ui.video.video_preview.MultiMusicPlayingActivity
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.ui.video.video_preview.adapter.CropMusicAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class EditMusicFragment : BaseFragment<FragmentEditVideoMusicBinding>() {
    override fun bindingView(): FragmentEditVideoMusicBinding {
        return FragmentEditVideoMusicBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({ R.layout.fragment_edit_video_music }, { true })

    lateinit var adapter: CropMusicAdapter

    override fun releaseData() {}

    override fun initConfiguration() {
        super.initConfiguration()
        adapter = CropMusicAdapter(requireActivity() as MultiMusicPlayingActivity)
        binding.rvMusic.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvMusic.adapter = adapter
    }

    override fun initListener() {
        super.initListener()
        binding.btnAddMusic.setOnClickListener {
            requireActivity().startActivityForResult(
                    SelectMusicActivity.getInstance(
                            requireContext(), (activity as VideoCreateActivity).draft
                    ),
                    Common.REQUEST_PICK_AUDIO
            )
        }
        binding.btnUseDefaultMusic.setOnClickListener {
            (requireActivity() as VideoCreateActivity).restoreDefaultMusic()
        }
        binding.btnAddMyMusic.setOnClickListener {
            requireActivity().startActivityForResult(
                    SelectMusicActivity.getInstance(
                            requireContext(), (activity as VideoCreateActivity).draft
                    ),
                    Common.REQUEST_PICK_AUDIO
            )
        }
    }

    override fun initTask() {
        super.initTask()
        updateMusics()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMusicSelectedListChanged(event: MusicSelectedChangeEvent) {
        Log.d("namsss", "onMusicSelectedListChanged: ")
        updateMusics()
        lifecycleScope.launchWhenResumed { (requireActivity() as VideoCreateActivity).saveDraft() }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMusicStateChanged(event: MusicStateChangedEvent) {
        adapter.notifyDataSetChanged()
    }

    private fun updateMusics() {
        adapter.updateMusics()
        when (adapter.itemCount) {
            0 -> {
                binding.btnAddMyMusic.visibility = View.VISIBLE
                binding.btnUseDefaultMusic.visibility = View.VISIBLE
                binding.tvMyMusic.visibility = View.VISIBLE
                binding.tvDefaultMusic.visibility = View.VISIBLE
                binding.btnAddMusic.visibility = View.GONE
            }
            MusicSetupPlayBack.MAX_MUSIC_COUNT -> {
                binding.btnAddMyMusic.visibility = View.GONE
                binding.btnUseDefaultMusic.visibility = View.GONE
                binding.tvMyMusic.visibility = View.GONE
                binding.tvDefaultMusic.visibility = View.GONE
                binding.btnAddMusic.visibility = View.GONE
            }
            else -> {
                binding.btnAddMyMusic.visibility = View.GONE
                binding.btnUseDefaultMusic.visibility = View.GONE
                binding.tvMyMusic.visibility = View.GONE
                binding.tvDefaultMusic.visibility = View.GONE
                binding.btnAddMusic.visibility = View.VISIBLE
            }
        }
    }


}