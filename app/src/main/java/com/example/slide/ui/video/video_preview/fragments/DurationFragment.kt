package com.example.slide.ui.video.video_preview.fragments

import androidx.fragment.app.Fragment
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.FragmentEditVideoDurationBinding
import com.example.slide.local.PreferencesHelper
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.ui.video.video_preview.dialog.CustomDurationDialogFragment
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar

class DurationFragment : BaseFragment<FragmentEditVideoDurationBinding>() {
    override fun bindingView(): FragmentEditVideoDurationBinding {
        return FragmentEditVideoDurationBinding.inflate(layoutInflater)
    }

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
        binding.rangeSeekBar.setProgress(processSeekBar)
        if (processFromUser > 6)
            binding.rangeSeekBar.setProcessCustomValue(processFromUser)
        setDurationTitle(
            (processSeekBar.toInt()+1),
            videoCreateActivity.myApplication.videoDataState.totalSecond
        )
    }

    override fun initListener() {
        super.initListener()
        binding.rangeSeekBar.setOnRangeChangedListener(object : OnRangeChangedListener {
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
                val duration = binding.rangeSeekBar.leftSeekBar.progress + 1
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
            binding.rangeSeekBar.setProcessCustomValue(MAX_DEFAULT_PROCESS_SEEK_BAR)
            binding.rangeSeekBar.setProgress((process - 1).toFloat())
        } else {
            binding.rangeSeekBar.setProcessCustomValue(process)
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
        binding.tvDurationSpeed.text = String.format(getString(R.string.format_speed_per_transition),speed)
        binding.tvDurationTotal.text = String.format(getString(R.string.format_total_speed),total)
    }

    override fun releaseData() {
        binding.rangeSeekBar.setOnRangeChangedListener(null)
    }

}