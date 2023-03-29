package com.example.slide.ui.video.video_preview.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.slide.MyApplication
import com.example.slide.R
import com.example.slide.base.BaseBottomBindingDialog
import com.example.slide.databinding.DialogVideoStickerBinding
import com.example.slide.framework.cutter.myrangeseekbar.AudioCutterView
import com.example.slide.framework.texttovideo.DrawableVideoFloatingItem
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.util.StringUtils

class ChangeTimeVideoStickerFragment : BaseBottomBindingDialog<DialogVideoStickerBinding>(), View.OnClickListener {

    private var duration = 0

    private var startTime = 0

    private var endTime = 0

    private var isFullTime = true

    private var drawableVideoSticker: DrawableVideoFloatingItem? = null

    companion object {

        const val TAG = "VideoStickerDialog"

        fun newInstance(drawableVideoSticker: DrawableVideoFloatingItem): ChangeTimeVideoStickerFragment {
            return ChangeTimeVideoStickerFragment().apply {
                this.drawableVideoSticker = drawableVideoSticker
            }
        }
    }

    override fun bindingView(): DialogVideoStickerBinding {
        return DialogVideoStickerBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_video_sticker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        duration =
            (requireActivity() as VideoCreateActivity).myApplication.videoDataState.totalSecond
        val localDrawableVideoSticker = drawableVideoSticker
        binding.seekBarSubtitle.count = duration
        if (localDrawableVideoSticker != null) {
            isFullTime = localDrawableVideoSticker.isFullTime
            if (isFullTime) {
                startTime = 0
                endTime = duration
            } else {
                startTime = localDrawableVideoSticker.startTime
                endTime = localDrawableVideoSticker.endTime
            }
            Log.d(TAG, "onViewCreated: " + startTime + " " + endTime)
            binding.seekBarSubtitle.minProgress = startTime
            binding.seekBarSubtitle.maxProgress = endTime
            binding.tvStartTime.text = StringUtils.getDurationDisplayFromSeconds(startTime)
            binding.tvEndTime.text = StringUtils.getDurationDisplayFromSeconds(endTime)
            binding.tvTime.text = StringUtils.getDurationDisplayFromSeconds(endTime - startTime)

            binding.btnClose.setOnClickListener(this)
            binding.btnCheck.setOnClickListener(this)
            binding.btnDecreaseStart.setOnClickListener(this)
            binding.btnIncreaseStart.setOnClickListener(this)
            binding.btnIncreaseEnd.setOnClickListener(this)
            binding.btnDecreaseEnd.setOnClickListener(this)
            timeSubtitleListener()
        } else
            dismiss()
    }


    private fun timeSubtitleListener() {
        binding.seekBarSubtitle.setOnValueChangedListener(object :
            AudioCutterView.OnValueChangedListener() {
            override fun onValueChanged(progress: Int, fromUser: Boolean) {
            }

            override fun onStartChanged(minProgress: Int, fromUser: Boolean) {
                super.onStartChanged(minProgress, fromUser)
                if (fromUser) {
                    startTime = minProgress
                    updateIsFullTime()
                    binding.tvTime.text =
                        StringUtils.getDurationDisplayFromSeconds(endTime - startTime)
                    binding.tvStartTime.text = StringUtils.getDurationDisplayFromSeconds(minProgress)
                    isFullTime =
                        startTime == 0 && endTime == MyApplication.getInstance().videoDataState.totalSecond
                }
            }

            override fun onEndChanged(maxProgress: Int, fromUser: Boolean) {
                super.onEndChanged(maxProgress, fromUser)
                if (fromUser) {
                    endTime = maxProgress
                    updateIsFullTime()
                    binding.tvTime.text =
                        StringUtils.getDurationDisplayFromSeconds(endTime - startTime)
                    binding.tvEndTime.text = StringUtils.getDurationDisplayFromSeconds(maxProgress)
                    isFullTime =
                        startTime == 0 && endTime == MyApplication.getInstance().videoDataState.totalSecond
                }
            }

        })
    }

    private fun updateIsFullTime() = startTime == 0 && endTime == duration

    override fun onClick(view: View) {
        when (view) {
            binding.btnClose -> {
                dismiss()
            }
            binding.btnCheck -> {
                drawableVideoSticker!!.startTime = startTime
                drawableVideoSticker!!.endTime = endTime
                drawableVideoSticker!!.isFullTime = isFullTime
                (requireActivity() as VideoCreateActivity).handleFloatingItem?.onFloatingItemChangedEvent()
                dismiss()
            }
            binding.btnDecreaseStart -> {
                if (startTime > 0)
                    binding.seekBarSubtitle.minProgress = --startTime
                binding.tvTime.text =
                    StringUtils.getDurationDisplayFromSeconds(endTime - startTime)
                binding.tvStartTime.text = StringUtils.getDurationDisplayFromSeconds(startTime)
                updateIsFullTime()
            }
            binding.btnIncreaseStart -> {
                if (endTime - startTime > 1)
                    binding.seekBarSubtitle.minProgress = ++startTime
                binding.tvTime.text =
                    StringUtils.getDurationDisplayFromSeconds(endTime - startTime)
                binding.tvStartTime.text = StringUtils.getDurationDisplayFromSeconds(startTime)
                updateIsFullTime()
            }
            binding.btnIncreaseEnd -> {
                if (endTime < duration)
                    binding.seekBarSubtitle.maxProgress = ++endTime
                binding.tvTime.text =
                    StringUtils.getDurationDisplayFromSeconds(endTime - startTime)
                binding.tvEndTime.text = StringUtils.getDurationDisplayFromSeconds(endTime)
                updateIsFullTime()
            }
            binding.btnDecreaseEnd -> {
                if (endTime - startTime > 1)
                    binding.seekBarSubtitle.maxProgress = --endTime
                binding.tvTime.text =
                    StringUtils.getDurationDisplayFromSeconds(endTime - startTime)
                binding.tvEndTime.text = StringUtils.getDurationDisplayFromSeconds(endTime)
                updateIsFullTime()
            }
        }
    }

}