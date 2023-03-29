package com.example.slide.ui.video.video_preview.dialog

import android.os.Bundle
import android.view.View
import com.example.slide.R
import com.example.slide.base.BaseBindingDialog
import com.example.slide.databinding.DialogVideoQualitySettingBinding
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.ui.video.video_preview.model.DataPreview
import com.example.slide.videolib.VideoConfig

class VideoQualityDialogFragment : BaseBindingDialog<DialogVideoQualitySettingBinding>(), View.OnClickListener {

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

    override fun bindingView(): DialogVideoQualitySettingBinding {
        return DialogVideoQualitySettingBinding.inflate(layoutInflater)
    }

    override fun extractData(it: Bundle) {
        super.extractData(it)
        dataPreview = arguments?.getSerializable(ARG_DATA_PREVIEW) as DataPreview
    }

    override fun initListener() {
        super.initListener()
        binding.btnRootView.setOnClickListener(this)
        binding.btn1080.setOnClickListener(this)
        binding.btn720.setOnClickListener(this)
        binding.btn480.setOnClickListener(this)
        binding.layoutContent.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        var isHandled = false
        when (view) {
            binding.btn1080 -> {
                isHandled =
                    (activity as VideoCreateActivity).saveVideoPreview(VideoConfig.VIDEO_QUALITY_1080)
            }
            binding.btn720 -> {
                isHandled =
                    (activity as VideoCreateActivity).saveVideoPreview(VideoConfig.VIDEO_QUALITY_720)
            }
            binding.btn480 -> {
                isHandled =
                    (activity as VideoCreateActivity).saveVideoPreview(VideoConfig.VIDEO_QUALITY_480)
            }
            binding.btnRootView -> {
                isHandled = true
            }
        }
        if (isHandled)
            dismiss()
    }
}