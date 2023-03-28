package com.example.slide.ui.video.video_preview.fragments

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slide.base.InitViewTools
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.ui.video.video_preview.adapter.ImageSelectedAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import kotlinx.android.synthetic.main.fragment_edit_video_image.*

class EditImageFragment : BaseFragment() {

    override fun initViewTools() = InitViewTools({ R.layout.fragment_edit_video_image })

    private var imageSelectedAdapter: ImageSelectedAdapter? = null

    private val createVideoActivity by lazy { activity as VideoCreateActivity }

    override fun initConfiguration() {
        imageSelected()
    }

    override fun initListener() {
        super.initListener()
        btn_add_new_images.setOnClickListener {
            createVideoActivity.goAddNewImages()
        }
        btn_edit_images.setOnClickListener {
            createVideoActivity.goSwapImages()
        }
    }

    private fun imageSelected() {
        val dragDropManager = RecyclerViewDragDropManager()
        imageSelectedAdapter = ImageSelectedAdapter((activity as VideoCreateActivity))
        recycler_view_selected.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recycler_view_selected.adapter = dragDropManager.createWrappedAdapter(imageSelectedAdapter!!)
        imageSelectedAdapter?.notifyDataSetChanged()
        dragDropManager.attachRecyclerView(recycler_view_selected)
    }

    fun notifyAdapter() {
        imageSelectedAdapter?.updateImages()
    }

    override fun releaseData() {}

}