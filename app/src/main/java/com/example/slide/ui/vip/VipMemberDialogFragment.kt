package com.example.slide.ui.vip

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.FragmentManager
import com.example.slide.R
import com.example.slide.base.BaseBindingDialog
import com.example.slide.databinding.DialogVipMemberBinding

class VipMemberDialogFragment : BaseBindingDialog<DialogVipMemberBinding>() {

    companion object {

        private const val TAG = "VipMemberDialogFragment"

        fun show(fragmentManager: FragmentManager){
            VipMemberDialogFragment().show(fragmentManager, TAG)
        }
    }

    override fun bindingView(): DialogVipMemberBinding {
        return DialogVipMemberBinding.inflate(layoutInflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        binding.btnOk.setOnClickListener {
            dismiss()
            requireActivity().finish()
        }
    }
}