package com.example.slide.ui.create

import android.os.Bundle
import android.view.View
import com.example.slide.R
import com.example.slide.base.BaseBindingDialog
import com.example.slide.databinding.DialogRestoreDraftErrorBinding

class RestoreDraftErrorDialog : BaseBindingDialog<DialogRestoreDraftErrorBinding>() {

    companion object {
        const val TAG = "RestoreDraftErrorDialog"
    }

    override val layoutId: Int
        get() = R.layout.dialog_restore_draft_error

    override fun bindingView(): DialogRestoreDraftErrorBinding {
        return DialogRestoreDraftErrorBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnGotIt.setOnClickListener {
            dismiss()
        }
    }

}