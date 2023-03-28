package com.example.slide.ui.video.video_preview.fragments

import androidx.fragment.app.Fragment
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.local.PreferencesHelper
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.ui.video.video_preview.dialog.CustomDurationDialogFragment
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import kotlinx.android.synthetic.main.fragment_edit_video_duration.*

class DurationFragment : BaseFragment() {

    override fun initViewTools() = InitViewTools({ R.layout.fragment_edit_video_duration })

    private var processFromUser: Int = 2

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(requireContext())
    }

    private var customDurationDialogFragment: CustomDurationDialogFragment? = null

    private val videoCreateActivity by lazy {
        activity as VideoCreateActivity
    }

    companion object {

        private const val VALUE_PROCESS_FROM_USER = "value process from user"

        const val MAX_DEFAULT_PROCESS_SEEK_BAR = 6

        const val MIN_CUSTOM_PROCESS_SEEK_BAR = 1

        const val MAX_CUSTOM_PROCESS_SEEK_BAR = 360
    }

    override fun initConfiguration() {
        super.initConfiguration()
        processFromUser = preferencesHelper.getSharedPreferences()
            .getInt(VALUE_PROCESS_FROM_USER, processFromUser)
        val processSeekBar = processFromUser - 1f
        rangeSeekBar.setProgress(processSeekBar)
        if (processFromUser > 6)
            rangeSeekBar.setProcessCustomValue(processFromUser)
        setDurationTitle(
            (processSeekBar.toInt()+1),
            videoCreateActivity.myApplication.videoDataState.totalSecond
        )
    }

    override fun initListener() {
        super.initListener()
        rangeSeekBar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }

            override fun onRangeChanged(
                view: RangeSeekBar,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean
            ) {

            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
                val duration = rangeSeekBar.leftSeekBar.progress + 1
                if (duration == MAX_DEFAULT_PROCESS_SEEK_BAR.toFloat())
                    CustomDurationDialogFragment.getInstance(duration.toInt()).show(
                        parentFragmentManager,
                        CustomDurationDialogFragment.TAG
                    )
                setSeekBarChange(duration.toInt())
            }
        })
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        if (childFragment is CustomDurationDialogFragment)
            customDurationDialogFragment = childFragment
    }

    fun setSeekBarChange(process: Int) {
        if (process <= MAX_DEFAULT_PROCESS_SEEK_BAR) {
            customDurationDialogFragment?.dismiss()
            rangeSeekBar.setProcessCustomValue(MAX_DEFAULT_PROCESS_SEEK_BAR)
            rangeSeekBar.setProgress((process - 1).toFloat())
        } else {
            rangeSeekBar.setProcessCustomValue(process)
        }
        preferencesHelper.getSharedPreferences().edit().putInt(VALUE_PROCESS_FROM_USER, process)
            .apply()
        videoCreateActivity.setDurationChanged(process)
        setDurationTitle(
            process,
            videoCreateActivity.myApplication.videoDataState.totalSecond
        )
    }

    private fun setDurationTitle(speed: Int, total: Int) {
        tv_duration_speed.text = String.format(getString(R.string.format_speed_per_transition),speed)
        tv_duration_total.text = String.format(getString(R.string.format_total_speed),total)
    }

    override fun releaseData() {
        rangeSeekBar.setOnRangeChangedListener(null)
    }

}