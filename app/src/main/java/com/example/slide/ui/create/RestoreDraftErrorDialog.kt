package com.example.slide.ui.create

import android.os.Bundle
import android.view.View
import com.example.slide.R
import com.example.slide.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_restore_draft_error.*

class RestoreDraftErrorDialog : BaseDialogFragment() {

    companion object {
        const val TAG = "RestoreDraftErrorDialog"
    }

    override val layoutId: Int
        get() = R.layout.dialog_restore_draft_error

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_got_it.setOnClickListener {
            dismiss()
        }
    }

}