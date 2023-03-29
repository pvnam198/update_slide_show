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
import com.example.slide.base.BaseBindingDialog
import com.example.slide.databinding.DialogSaveDraftBinding

class SaveDraftDialog : BaseBindingDialog<DialogSaveDraftBinding>() {

    private lateinit var onSaveDraftListener: OnSaveDraftListener

    private var isCheck = false

    override val layoutId: Int
        get() = R.layout.dialog_save_draft

    override fun bindingView(): DialogSaveDraftBinding {
        return DialogSaveDraftBinding.inflate(layoutInflater)
    }

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
        binding.root.setOnClickListener { dismiss() }
        binding.btnExitDirectly.setOnClickListener {
            val checkWidth = resources.getDimensionPixelSize(R.dimen.check_width).toFloat()
            if (isCheck) {
                binding.btnExitDirectly.animate().translationX(0f).setDuration(200)
                    .setInterpolator(AccelerateDecelerateInterpolator()).start()
                binding.btnCheck.animate().alpha(0f).setDuration(200).start()
            } else {
                binding.btnExitDirectly.animate().translationX(-checkWidth).setDuration(200)
                    .setInterpolator(AccelerateDecelerateInterpolator()).start()
                binding.btnCheck.alpha = 0f
                binding.btnCheck.animate().alpha(1f).setDuration(200).start()
            }
            isCheck = !isCheck
        }

        binding.btnSave.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                onSaveDraftListener.onSaveAsDraft()
            }

        }
        binding.btnCheck.setOnClickListener {
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