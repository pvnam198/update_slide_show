package com.example.slide.ui.select_music.default

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.FragmentDefaultMusicBinding
import com.example.slide.ui.select_music.SelectMusicActivity
import com.example.slide.ui.video.video_preview.MusicProvider

class DefaultMusicFragment : BaseFragment<FragmentDefaultMusicBinding>() {

    private lateinit var defaultMusicAdapter: DefaultMusicAdapter
    override fun bindingView(): FragmentDefaultMusicBinding {
        return FragmentDefaultMusicBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({ R.layout.fragment_default_music })

    override fun initConfiguration() {
        super.initConfiguration()
        defaultMusicAdapter = DefaultMusicAdapter(
            MusicProvider.defaultMusics(),
            requireContext(),
            onItemClick = { defaultMusic ->
                (requireActivity() as SelectMusicActivity).onDefaultMusicSelected(defaultMusic)
            })
        binding.rvDefaultMusic.adapter = defaultMusicAdapter
        binding.rvDefaultMusic.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun initListener() {
        super.initListener()
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
    }

    override fun releaseData() {
    }
}