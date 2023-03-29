package com.example.slide.ui.video.video_preview.fragments

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slide.base.InitViewTools
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.databinding.FragmentEditVideoTransitionBinding
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.ui.video.video_preview.adapter.EffectAdapter

class TransitionFragment() : BaseFragment<FragmentEditVideoTransitionBinding>() {
    override fun bindingView(): FragmentEditVideoTransitionBinding {
        return FragmentEditVideoTransitionBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({ R.layout.fragment_edit_video_transition })

    override fun initConfiguration() {
        super.initConfiguration()

        val effectAdapter = EffectAdapter(activity as VideoCreateActivity)
        binding.recyclerEffect.adapter = effectAdapter
        binding.recyclerEffect.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL, false
        )
    }

    override fun releaseData() {}

}