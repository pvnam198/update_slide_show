package com.example.slide.ui.video.video_preview.fragments

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slide.base.InitViewTools
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.databinding.FragmentEditVideoImageBinding
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.ui.video.video_preview.adapter.ImageSelectedAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager

class EditImageFragment : BaseFragment<FragmentEditVideoImageBinding>() {
    override fun bindingView(): FragmentEditVideoImageBinding {
        return FragmentEditVideoImageBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({ R.layout.fragment_edit_video_image })

    private var imageSelectedAdapter: ImageSelectedAdapter? = null

    private val createVideoActivity by lazy { activity as VideoCreateActivity }

    override fun initConfiguration() {
        imageSelected()
    }

    override fun initListener() {
        super.initListener()
        binding.btnAddNewImages.setOnClickListener {
            createVideoActivity.goAddNewImages()
        }
        binding.btnEditImages.setOnClickListener {
            createVideoActivity.goSwapImages()
        }
    }

    private fun imageSelected() {
        val dragDropManager = RecyclerViewDragDropManager()
        imageSelectedAdapter = ImageSelectedAdapter((activity as VideoCreateActivity))
        binding.recyclerViewSelected.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewSelected.adapter = dragDropManager.createWrappedAdapter(imageSelectedAdapter!!)
        imageSelectedAdapter?.notifyDataSetChanged()
        dragDropManager.attachRecyclerView(binding.recyclerViewSelected)
    }

    fun notifyAdapter() {
        imageSelectedAdapter?.updateImages()
    }

    override fun releaseData() {}

}