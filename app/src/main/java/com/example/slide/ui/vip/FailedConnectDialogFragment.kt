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
import com.example.slide.databinding.DialogFailedConnectInternetBinding

class FailedConnectDialogFragment : BaseBindingDialog<DialogFailedConnectInternetBinding>() {

    companion object {

        private const val TAG = "FailedConnectDialogFrag"

        fun show(fragmentManager: FragmentManager){
            FailedConnectDialogFragment().show(fragmentManager, TAG)
        }
    }

    override fun bindingView(): DialogFailedConnectInternetBinding {
        return DialogFailedConnectInternetBinding.inflate(layoutInflater)
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
        binding.btnOk.setOnClickListener {
            (requireActivity() as VipActivity).connectToPlayBillingService()
            dismiss()
        }
    }

}