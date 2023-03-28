package com.example.slide.ui.video.video_preview.fragments

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.ui.video.video_preview.adapter.FrameAdapter
import kotlinx.android.synthetic.main.fragment_edit_video_frame.*

class FrameFragment : BaseFragment() {

    override fun initViewTools() = InitViewTools({ R.layout.fragment_edit_video_frame })

    private lateinit var frameAdapter: FrameAdapter

    private val handler by lazy {
        Handler(Looper.myLooper()!!)
    }

    private val videoCreateActivity by lazy {
        activity as VideoCreateActivity
    }

    override fun initConfiguration() {
        super.initConfiguration()
        initFrameList()
    }

    private fun initFrameList() {
        frameAdapter = FrameAdapter(videoCreateActivity)
        rv_frame.adapter = frameAdapter
        rv_frame.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        if (videoCreateActivity.getFrameSelectedPosition() != 0) {
            val position = videoCreateActivity.getFrameSelectedPosition()
            handler.postDelayed({rv_frame.smoothScrollToPosition(position)}, 300)
            frameAdapter.reinstallFrame(position)
        } else {
            frameAdapter.reinstallFrame(videoCreateActivity.dataPreview.videoFrame)
        }
    }

    override fun releaseData() {
        handler.removeCallbacksAndMessages(null)
    }

}