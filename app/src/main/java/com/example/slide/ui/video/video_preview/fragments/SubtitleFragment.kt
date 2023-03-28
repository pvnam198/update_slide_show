package com.example.slide.ui.video.video_preview.fragments

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.framework.texttovideo.VideoTextFloatingItem
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.ui.video.video_preview.adapter.SubTitleAdapter
import com.example.slide.ui.video.video_preview.callback.HandleFloatItem
import kotlinx.android.synthetic.main.fragment_edit_video_subtitle.*

class SubtitleFragment : BaseFragment(), HandleFloatItem {

    override fun initViewTools() =
        InitViewTools({ R.layout.fragment_edit_video_subtitle })

    private lateinit var subTitleAdapter: SubTitleAdapter

    private val videoCreateActivity by lazy {
        activity as VideoCreateActivity
    }

    override fun onResume() {
        super.onResume()
        subTitleAdapter.changeTotalTime(myApplication.videoDataState.totalSecond)
        subTitleAdapter.updateData(videoCreateActivity.getVideoTextStickers())
    }

    override fun initConfiguration() {
        super.initConfiguration()
        videoCreateActivity.handleFloatingItem = this
        subTitleAdapter = SubTitleAdapter(myApplication.videoDataState.totalSecond,
            getSubtitles(),
            removeListener = { textSticker ->
                videoCreateActivity.removeSub(textSticker)
            }, editListener = { textSticker ->
                videoCreateActivity.editSubTitle(textSticker)
            })
        rv_text.layoutManager = LinearLayoutManager(requireContext())
        rv_text.adapter = subTitleAdapter
    }

    override fun initListener() {
        super.initListener()
        btn_add_text.setOnClickListener {
            showKeyboard()
            videoCreateActivity.showAddTextFragment()
        }
    }

    fun durationChanged() {
        if (isBind)
            subTitleAdapter.changeTotalTime(myApplication.videoDataState.totalSecond)
    }

    override fun releaseData() {

    }

    private fun getSubtitles(): ArrayList<VideoTextFloatingItem> {
        return (requireActivity() as VideoCreateActivity).getVideoTextStickers()
    }

    private fun showKeyboard() {
        val inputMethodManager: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    override fun onFloatingItemChangedEvent() {
        subTitleAdapter.updateData(videoCreateActivity.getVideoTextStickers())
        lifecycleScope.launchWhenResumed { (requireActivity() as VideoCreateActivity).saveDraft() }
    }


}