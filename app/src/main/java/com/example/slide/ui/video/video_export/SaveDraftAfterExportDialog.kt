package com.example.slide.ui.video.video_export

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.slide.R
import com.example.slide.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_save_draft_2.*

class SaveDraftAfterExportDialog : BaseDialogFragment() {

    private lateinit var onSaveDraftListener: OnSaveDraftListener

    override val layoutId: Int
        get() = R.layout.dialog_save_draft_2

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onSaveDraftListener = activity as OnSaveDraftListener
        } catch (e: ClassCastException) {
            Log.d("SaveDraft", e.message.toString())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_yes.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                onSaveDraftListener.onSaveAsDraft()
            }

        }
        btn_no.setOnClickListener {
            lifecycleScope.launchWhenResumed { onSaveDraftListener.onDiscard() }
        }
    }

    interface OnSaveDraftListener {
        fun onSaveAsDraft()
        fun onDiscard()
    }

    companion object {
        const val TAG = "SaveDraftAfterExportDialog"

        fun createInstance(): SaveDraftAfterExportDialog {
            return SaveDraftAfterExportDialog()
        }
    }
}