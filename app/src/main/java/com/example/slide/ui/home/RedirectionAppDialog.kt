package com.example.slide.ui.home

import com.example.slide.base.BaseBindingDialog
import com.example.slide.databinding.DialogRedirectionAppBinding

class RedirectionAppDialog : BaseBindingDialog<DialogRedirectionAppBinding>() {

    override fun bindingView() = DialogRedirectionAppBinding.inflate(layoutInflater)

    companion object {

        const val TAG = "RedirectionAppDialog"

        fun newInstance(): RedirectionAppDialog {
            return RedirectionAppDialog()
        }
    }

}