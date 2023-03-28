package com.example.slide.ui.video.video_preview.dialog

import android.os.Bundle
import android.view.View
import com.example.slide.R
import com.example.slide.base.BaseDialogFragment
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.ui.video.video_preview.model.DataPreview
import com.example.slide.videolib.VideoConfig
import kotlinx.android.synthetic.main.dialog_video_quality_setting.*

class VideoQualityDialogFragment : BaseDialogFragment(), View.OnClickListener {

    private lateinit var dataPreview: DataPreview

    companion object {

        const val TAG = "SettingDialogFragment"

        private const val ARG_DATA_PREVIEW = "data_preview"

        fun getInstance(dataPreview: DataPreview): VideoQualityDialogFragment {
            val settingDialogFragment = VideoQualityDialogFragment()
            val args = Bundle()
            args.putSerializable(ARG_DATA_PREVIEW, dataPreview)
            settingDialogFragment.arguments = args
            return settingDialogFragment
        }
    }

    override val layoutId: Int
        get() = R.layout.dialog_video_quality_setting

    override fun extractData(it: Bundle) {
        super.extractData(it)
        dataPreview = arguments?.getSerializable(ARG_DATA_PREVIEW) as DataPreview
    }

    override fun initListener() {
        super.initListener()
        btn_root_view.setOnClickListener(this)
        btn_1080.setOnClickListener(this)
        btn_720.setOnClickListener(this)
        btn_480.setOnClickListener(this)
        layout_content.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        var isHandled = false
        when (view) {
            btn_1080 -> {
                isHandled =
                    (activity as VideoCreateActivity).saveVideoPreview(VideoConfig.VIDEO_QUALITY_1080)
            }
            btn_720 -> {
                isHandled =
                    (activity as VideoCreateActivity).saveVideoPreview(VideoConfig.VIDEO_QUALITY_720)
            }
            btn_480 -> {
                isHandled =
                    (activity as VideoCreateActivity).saveVideoPreview(VideoConfig.VIDEO_QUALITY_480)
            }
            btn_root_view -> {
                isHandled = true
            }
        }
        if (isHandled)
            dismiss()
    }
}