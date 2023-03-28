package com.example.slide.ui.video.video_export

import com.example.slide.R
import com.example.slide.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_export_fail.*

class ExportFailedDialog : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.dialog_export_fail

    companion object {
        const val TAG = "ExportFailedDialog"
    }

    override fun initListener() {
        super.initListener()
        btn_ok.setOnClickListener { (requireActivity() as ExportVideoActivity).finish() }
    }

}