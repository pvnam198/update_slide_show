package com.example.slide.ui.video.video_preview.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.slide.MyApplication
import com.example.slide.R
import com.example.slide.framework.cutter.myrangeseekbar.AudioCutterView
import com.example.slide.framework.texttovideo.DrawableVideoFloatingItem
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.util.StringUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_video_sticker.*

class ChangeTimeVideoStickerFragment : BottomSheetDialogFragment(), View.OnClickListener {

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
        seek_bar_subtitle.count = duration
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
            seek_bar_subtitle.minProgress = startTime
            seek_bar_subtitle.maxProgress = endTime
            tv_start_time.text = StringUtils.getDurationDisplayFromSeconds(startTime)
            tv_end_time.text = StringUtils.getDurationDisplayFromSeconds(endTime)
            tv_time.text = StringUtils.getDurationDisplayFromSeconds(endTime - startTime)

            btn_close.setOnClickListener(this)
            btn_check.setOnClickListener(this)
            btn_decrease_start.setOnClickListener(this)
            btn_increase_start.setOnClickListener(this)
            btn_increase_end.setOnClickListener(this)
            btn_decrease_end.setOnClickListener(this)
            timeSubtitleListener()
        } else
            dismiss()
    }


    private fun timeSubtitleListener() {
        seek_bar_subtitle.setOnValueChangedListener(object :
            AudioCutterView.OnValueChangedListener() {
            override fun onValueChanged(progress: Int, fromUser: Boolean) {
            }

            override fun onStartChanged(minProgress: Int, fromUser: Boolean) {
                super.onStartChanged(minProgress, fromUser)
                if (fromUser) {
                    startTime = minProgress
                    updateIsFullTime()
                    tv_time.text =
                        StringUtils.getDurationDisplayFromSeconds(endTime - startTime)
                    tv_start_time.text = StringUtils.getDurationDisplayFromSeconds(minProgress)
                    isFullTime =
                        startTime == 0 && endTime == MyApplication.getInstance().videoDataState.totalSecond
                }
            }

            override fun onEndChanged(maxProgress: Int, fromUser: Boolean) {
                super.onEndChanged(maxProgress, fromUser)
                if (fromUser) {
                    endTime = maxProgress
                    updateIsFullTime()
                    tv_time.text =
                        StringUtils.getDurationDisplayFromSeconds(endTime - startTime)
                    tv_end_time.text = StringUtils.getDurationDisplayFromSeconds(maxProgress)
                    isFullTime =
                        startTime == 0 && endTime == MyApplication.getInstance().videoDataState.totalSecond
                }
            }

        })
    }

    private fun updateIsFullTime() = startTime == 0 && endTime == duration

    override fun onClick(view: View) {
        when (view) {
            btn_close -> {
                dismiss()
            }
            btn_check -> {
                drawableVideoSticker!!.startTime = startTime
                drawableVideoSticker!!.endTime = endTime
                drawableVideoSticker!!.isFullTime = isFullTime
                (requireActivity() as VideoCreateActivity).handleFloatingItem?.onFloatingItemChangedEvent()
                dismiss()
            }
            btn_decrease_start -> {
                if (startTime > 0)
                    seek_bar_subtitle.minProgress = --startTime
                tv_time.text =
                    StringUtils.getDurationDisplayFromSeconds(endTime - startTime)
                tv_start_time.text = StringUtils.getDurationDisplayFromSeconds(startTime)
                updateIsFullTime()
            }
            btn_increase_start -> {
                if (endTime - startTime > 1)
                    seek_bar_subtitle.minProgress = ++startTime
                tv_time.text =
                    StringUtils.getDurationDisplayFromSeconds(endTime - startTime)
                tv_start_time.text = StringUtils.getDurationDisplayFromSeconds(startTime)
                updateIsFullTime()
            }
            btn_increase_end -> {
                if (endTime < duration)
                    seek_bar_subtitle.maxProgress = ++endTime
                tv_time.text =
                    StringUtils.getDurationDisplayFromSeconds(endTime - startTime)
                tv_end_time.text = StringUtils.getDurationDisplayFromSeconds(endTime)
                updateIsFullTime()
            }
            btn_decrease_end -> {
                if (endTime - startTime > 1)
                    seek_bar_subtitle.maxProgress = --endTime
                tv_time.text =
                    StringUtils.getDurationDisplayFromSeconds(endTime - startTime)
                tv_end_time.text = StringUtils.getDurationDisplayFromSeconds(endTime)
                updateIsFullTime()
            }
        }
    }

}