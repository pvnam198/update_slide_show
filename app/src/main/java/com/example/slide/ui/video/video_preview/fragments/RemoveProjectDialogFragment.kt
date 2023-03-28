package com.example.slide.ui.video.video_preview.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.slide.R
import com.example.slide.ui.home.MainActivity
import kotlinx.android.synthetic.main.dialog_remove_project.*

class RemoveProjectDialogFragment : DialogFragment(), View.OnClickListener {

    companion object {
        fun getInstance(): RemoveProjectDialogFragment {
            return RemoveProjectDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_remove_project, container)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_cancel.setOnClickListener(this)
        btn_ok.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view) {
            btn_cancel -> {
                dismiss()
            }
            btn_ok -> {
                dismiss()
                requireContext().startActivity(MainActivity.getCallingIntent(requireContext()))
            }
        }
    }
}