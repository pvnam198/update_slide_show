package com.example.slide.ui.video.video_export

import com.example.slide.R
import com.example.slide.base.BaseBindingDialog
import com.example.slide.databinding.DialogExportFailBinding

class ExportFailedDialog : BaseBindingDialog<DialogExportFailBinding>() {

    override val layoutId: Int
        get() = R.layout.dialog_export_fail

    override fun bindingView(): DialogExportFailBinding {
        return DialogExportFailBinding.inflate(layoutInflater)
    }

    companion object {
        const val TAG = "ExportFailedDialog"
    }

    override fun initListener() {
        super.initListener()
        binding.btnOk.setOnClickListener { (requireActivity() as ExportVideoActivity).finish() }
    }

}