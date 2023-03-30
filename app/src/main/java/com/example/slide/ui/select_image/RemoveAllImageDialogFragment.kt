package com.example.slide.ui.select_image

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
import com.example.slide.base.BaseBindingDialog
import com.example.slide.databinding.DialogClearImageSelectedBinding

class RemoveAllImageDialogFragment : BaseBindingDialog<DialogClearImageSelectedBinding>(), View.OnClickListener {

    companion object {
        fun getInstance(): RemoveAllImageDialogFragment {
            return RemoveAllImageDialogFragment()
        }
    }

    private lateinit var selectFragment: SelectFragment
    override fun bindingView(): DialogClearImageSelectedBinding {
        return DialogClearImageSelectedBinding.inflate(layoutInflater)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectFragment =
            requireActivity().supportFragmentManager.findFragmentByTag(SelectFragment::class.java.name) as SelectFragment
        binding.btnYes.setOnClickListener(this)
        binding.btnNo.setOnClickListener(this)
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

    override fun onClick(view: View) {
        when (view) {
            binding.btnYes -> {
                selectFragment.removeAllSelectedImage()
                dismiss()
            }
            binding.btnNo -> {
                dismiss()
            }
        }
    }
}