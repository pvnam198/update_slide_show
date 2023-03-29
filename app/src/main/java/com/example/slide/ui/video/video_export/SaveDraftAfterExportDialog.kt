package com.example.slide.ui.video.video_export

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.slide.R
import com.example.slide.base.BaseBindingDialog
import com.example.slide.databinding.DialogSaveDraft2Binding

class SaveDraftAfterExportDialog : BaseBindingDialog<DialogSaveDraft2Binding>() {

    private lateinit var onSaveDraftListener: OnSaveDraftListener

    override val layoutId: Int
        get() = R.layout.dialog_save_draft_2

    override fun bindingView(): DialogSaveDraft2Binding {
        return DialogSaveDraft2Binding.inflate(layoutInflater)
    }

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
        binding.btnYes.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                onSaveDraftListener.onSaveAsDraft()
            }

        }
        binding.btnNo.setOnClickListener {
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