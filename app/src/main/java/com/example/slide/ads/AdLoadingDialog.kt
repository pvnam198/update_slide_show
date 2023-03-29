package com.example.slide.ads

import android.content.DialogInterface
import android.view.KeyEvent
import com.example.slide.R
import com.example.slide.base.BaseBindingDialog
import com.example.slide.databinding.DialogAdLoadingBinding

class AdLoadingDialog : BaseBindingDialog<DialogAdLoadingBinding>() {

    companion object {

        const val TAG = "AdLoadingDialog"

        fun getInstance(): AdLoadingDialog {
            return AdLoadingDialog()
        }
    }

    override val layoutId: Int
        get() = R.layout.dialog_ad_loading

    override fun bindingView(): DialogAdLoadingBinding {
        return DialogAdLoadingBinding.inflate(layoutInflater)
    }

    override fun onResume() {
        super.onResume()
        dialog?.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    if (event?.action != KeyEvent.ACTION_DOWN)
                        return true
                }
                return false
            }
        })
    }

}