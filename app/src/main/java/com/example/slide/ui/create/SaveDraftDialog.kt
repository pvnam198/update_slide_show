package com.example.slide.ui.create

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.slide.R
import com.example.slide.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_save_draft.*

class SaveDraftDialog : BaseDialogFragment() {

    private lateinit var onSaveDraftListener: OnSaveDraftListener

    private var isCheck = false

    override val layoutId: Int
        get() = R.layout.dialog_save_draft

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onSaveDraftListener = activity as OnSaveDraftListener
        } catch (e: ClassCastException) {
            Log.d("SaveDraft", e.message.toString())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        root.setOnClickListener { dismiss() }
        btn_exit_directly.setOnClickListener {
            val checkWidth = resources.getDimensionPixelSize(R.dimen.check_width).toFloat()
            if (isCheck) {
                btn_exit_directly.animate().translationX(0f).setDuration(200)
                    .setInterpolator(AccelerateDecelerateInterpolator()).start()
                btn_check.animate().alpha(0f).setDuration(200).start()
            } else {
                btn_exit_directly.animate().translationX(-checkWidth).setDuration(200)
                    .setInterpolator(AccelerateDecelerateInterpolator()).start()
                btn_check.alpha = 0f
                btn_check.animate().alpha(1f).setDuration(200).start()
            }
            isCheck = !isCheck
        }

        btn_save.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                onSaveDraftListener.onSaveAsDraft()
            }

        }
        btn_check.setOnClickListener {
            lifecycleScope.launchWhenResumed { onSaveDraftListener.onDiscard() }
        }
    }

    interface OnSaveDraftListener {
        fun onSaveAsDraft()
        fun onDiscard()
    }

    companion object {
        const val TAG = "SaveDraftDialog"

        fun createInstance(): SaveDraftDialog {
            return SaveDraftDialog()
        }
    }
}