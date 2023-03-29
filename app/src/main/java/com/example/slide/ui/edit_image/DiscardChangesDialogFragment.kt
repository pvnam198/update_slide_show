package com.example.slide.ui.edit_image

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.example.slide.R
import com.example.slide.base.BaseBindingDialog
import com.example.slide.databinding.DialogStopExportBinding

class DiscardChangesDialogFragment : BaseBindingDialog<DialogStopExportBinding>(), View.OnClickListener {

    companion object{

        const val TAG = "DiscardChangesDialogFragment"

        fun getInstance(): DiscardChangesDialogFragment {
            return DiscardChangesDialogFragment()
        }

    }

    override fun bindingView(): DialogStopExportBinding {
        return DialogStopExportBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_stop_export, container)
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
        binding.btnCancel.setOnClickListener(this)
        binding.btnOk.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnCancel -> {
                dismiss()
            }
            binding.btnOk -> {
                dismiss()
                (requireActivity() as EditImageActivity).discardChanges()
            }
        }
    }

}