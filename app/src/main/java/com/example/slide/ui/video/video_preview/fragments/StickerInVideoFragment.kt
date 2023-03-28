package com.example.slide.ui.video.video_preview.fragments

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.ui.video.video_preview.adapter.StickerInVideoAdapter
import com.example.slide.ui.video.video_preview.callback.HandleFloatItem
import kotlinx.android.synthetic.main.fragment_sticker_in_video.*

class StickerInVideoFragment : BaseFragment(), HandleFloatItem {

    private lateinit var stickerInVideoAdapter: StickerInVideoAdapter

    companion object {
        private const val TAG = "StickerInVideoFragment"
    }

    override fun initViewTools() = InitViewTools({ R.layout.fragment_sticker_in_video })

    override fun initConfiguration() {
        super.initConfiguration()
        (requireActivity() as VideoCreateActivity).handleFloatingItem = this
        stickerInVideoAdapter = StickerInVideoAdapter(
                editItem = {
                    (requireActivity() as VideoCreateActivity).openChangeTimeVideoStickerDialog(it)
                },
                removeItem = {
                    (requireActivity() as VideoCreateActivity).removeDrawableVideoSticker(it)
                })
        rv_sticker.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv_sticker.adapter = stickerInVideoAdapter
    }

    override fun onResume() {
        super.onResume()
        stickerInVideoAdapter.updateData((requireActivity() as VideoCreateActivity).getDrawableVideoStickers())
    }

    override fun initListener() {
        super.initListener()
        btn_add.setOnClickListener {
            (requireActivity() as VideoCreateActivity).openStickerFragment()
        }
    }

    override fun releaseData() {

    }

    override fun onFloatingItemChangedEvent() {
        stickerInVideoAdapter.updateData((requireActivity() as VideoCreateActivity).getDrawableVideoStickers())
        lifecycleScope.launchWhenResumed { (requireActivity() as VideoCreateActivity).saveDraft() }
    }
}