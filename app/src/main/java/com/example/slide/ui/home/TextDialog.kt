package com.example.slide.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.base.BaseBindingDialog
import com.example.slide.databinding.DialogTextBinding
import com.example.slide.framework.thirdparty.DialogApp


class TextDialog : BaseBindingDialog<DialogTextBinding>() {
    companion object {

        private const val EXTRA_DIALOG = "dialog"

        public fun newInstance(dialogApp: DialogApp): TextDialog {
            val dialog = TextDialog()
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_DIALOG, dialogApp)
            dialog.arguments = bundle
            return dialog
        }
    }

    private var dialogApp: DialogApp? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(EXTRA_DIALOG, dialogApp)
    }

    override fun bindingView(): DialogTextBinding {
        return DialogTextBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments ?: savedInstanceState
        bundle ?: return
        dialogApp = bundle.getSerializable(EXTRA_DIALOG) as DialogApp
        binding.btnClose.setOnClickListener { dismiss() }
        dialogApp?.let { dialogInfo ->
            Glide.with(this).load(dialogInfo.icon).into(binding.ivIcon)
            binding.tvTitle.text = dialogInfo.name
            binding.tvContent.text = dialogInfo.content
            binding.btnInstall.setOnClickListener {
                (activity as MainActivity?)?.goToAppFromDialog(dialogInfo)
            }
        }


    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

}