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
import com.example.slide.event.MusicSelectedChangeEvent
import com.example.slide.event.MusicStateChangedEvent
import com.example.slide.music_engine.MusicSetupPlayBack
import com.example.slide.ui.select_music.SelectMusicActivity
import com.example.slide.ui.video.video_preview.MultiMusicPlayingActivity
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.ui.video.video_preview.adapter.CropMusicAdapter
import kotlinx.android.synthetic.main.fragment_edit_video_music.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class EditMusicFragment : BaseFragment() {

    override fun initViewTools() = InitViewTools({ R.layout.fragment_edit_video_music }, { true })

    lateinit var adapter: CropMusicAdapter

    override fun releaseData() {}

    override fun initConfiguration() {
        super.initConfiguration()
        adapter = CropMusicAdapter(requireActivity() as MultiMusicPlayingActivity)
        rv_music.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rv_music.adapter = adapter
    }

    override fun initListener() {
        super.initListener()
        btn_add_music.setOnClickListener {
            requireActivity().startActivityForResult(
                    SelectMusicActivity.getInstance(
                            requireContext(), (activity as VideoCreateActivity).draft
                    ),
                    Common.REQUEST_PICK_AUDIO
            )
        }
        btn_use_default_music.setOnClickListener {
            (requireActivity() as VideoCreateActivity).restoreDefaultMusic()
        }
        btn_add_my_music.setOnClickListener {
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
                btn_add_my_music.visibility = View.VISIBLE
                btn_use_default_music.visibility = View.VISIBLE
                tv_my_music.visibility = View.VISIBLE
                tv_default_music.visibility = View.VISIBLE
                btn_add_music.visibility = View.GONE
            }
            MusicSetupPlayBack.MAX_MUSIC_COUNT -> {
                btn_add_my_music.visibility = View.GONE
                btn_use_default_music.visibility = View.GONE
                tv_my_music.visibility = View.GONE
                tv_default_music.visibility = View.GONE
                btn_add_music.visibility = View.GONE
            }
            else -> {
                btn_add_my_music.visibility = View.GONE
                btn_use_default_music.visibility = View.GONE
                tv_my_music.visibility = View.GONE
                tv_default_music.visibility = View.GONE
                btn_add_music.visibility = View.VISIBLE
            }
        }
    }


}