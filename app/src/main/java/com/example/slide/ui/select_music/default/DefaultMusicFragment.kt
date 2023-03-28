package com.example.slide.ui.select_music.default

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.ui.select_music.SelectMusicActivity
import com.example.slide.ui.video.video_preview.MusicProvider
import kotlinx.android.synthetic.main.fragment_default_music.*

class DefaultMusicFragment : BaseFragment() {

    private lateinit var defaultMusicAdapter: DefaultMusicAdapter

    override fun initViewTools() = InitViewTools({ R.layout.fragment_default_music })

    override fun initConfiguration() {
        super.initConfiguration()
        defaultMusicAdapter = DefaultMusicAdapter(
            MusicProvider.defaultMusics(),
            requireContext(),
            onItemClick = { defaultMusic ->
                (requireActivity() as SelectMusicActivity).onDefaultMusicSelected(defaultMusic)
            })
        rv_default_music.adapter = defaultMusicAdapter
        rv_default_music.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener { requireActivity().onBackPressed() }
    }

    override fun releaseData() {
    }
}