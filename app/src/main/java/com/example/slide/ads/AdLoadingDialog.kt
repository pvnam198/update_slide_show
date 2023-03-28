package com.example.slide.ads

import android.content.DialogInterface
import android.view.KeyEvent
import com.example.slide.R
import com.example.slide.base.BaseDialogFragment

class AdLoadingDialog : BaseDialogFragment() {

    companion object {

        const val TAG = "AdLoadingDialog"

        fun getInstance(): AdLoadingDialog {
            return AdLoadingDialog()
        }
    }

    override val layoutId: Int
        get() = R.layout.dialog_ad_loading

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