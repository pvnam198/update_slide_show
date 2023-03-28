package com.example.slide.ui.video.video_preview.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.slide.R
import com.example.slide.ui.video.video_preview.fragments.DurationFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_custom_duration.*

class CustomDurationDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var behavior: BottomSheetBehavior<View>? = null

    private var process = 6

    private var durationFragment: DurationFragment? = null

    companion object {

        private const val ARG_PROCESS = "process"

        const val TAG: String = "CustomDurationDialogFragment"

        fun getInstance(process: Int): CustomDurationDialogFragment {
            return CustomDurationDialogFragment().apply {
                arguments = bundleOf(ARG_PROCESS to process)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        process = requireArguments().getInt(ARG_PROCESS, 6)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_custom_duration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragment = parentFragmentManager.findFragmentByTag(DurationFragment::class.java.name)
        if (fragment is DurationFragment) {
            durationFragment = fragment
        }

        edt_value.setText(process.toString())
        btn_ok.setOnClickListener(this)
        btn_decrease.setOnClickListener(this)
        btn_increase.setOnClickListener(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet =
                (it as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            behavior?.addBottomSheetCallback(callback)
        }
        return dialog
    }

    private var callback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    override fun onClick(view: View) {
        when (view) {
            btn_ok -> {
                process = edt_value.text.toString().toInt()
                if (process < DurationFragment.MIN_CUSTOM_PROCESS_SEEK_BAR || process > DurationFragment.MAX_CUSTOM_PROCESS_SEEK_BAR) {

                    if (process < DurationFragment.MIN_CUSTOM_PROCESS_SEEK_BAR) {
                        edt_value.setText(DurationFragment.MIN_CUSTOM_PROCESS_SEEK_BAR.toString())
                    } else {
                        edt_value.setText(DurationFragment.MAX_CUSTOM_PROCESS_SEEK_BAR.toString())
                    }
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.msg_custom_duration),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    durationFragment?.setSeekBarChange(process)
                    dismiss()
                }
            }
            btn_decrease -> {
                if (process > 0)
                    edt_value.setText((--process).toString())

            }
            btn_increase -> {
                edt_value.setText((++process).toString())
            }
        }
    }

}