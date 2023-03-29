package com.example.slide.ui.vip

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
import com.example.slide.databinding.DialogVipComfirmExitBinding

class BecomeVipDialogFragment: BaseBindingDialog<DialogVipComfirmExitBinding>(), View.OnClickListener {

    companion object {

        const val TAG = "BecomeVipDialogFragment"

        fun newInstance(): BecomeVipDialogFragment {
            return BecomeVipDialogFragment()
        }
    }

    override fun bindingView(): DialogVipComfirmExitBinding {
        return DialogVipComfirmExitBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_vip_comfirm_exit, container)
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

        binding.btnGiveUp.setOnClickListener(this)
        binding.btnBecomeVip.setOnClickListener(this)
        binding.tvPrice.text = ""
    }

    override fun onClick(view: View?) {
        when(view){
            binding.btnGiveUp->{
                dismiss()
            }
            binding.btnBecomeVip->{
                (requireActivity() as VipActivity).tryPurchaseAgain()
                dismiss()
            }
        }
    }
}