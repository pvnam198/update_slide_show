package com.example.slide.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.framework.thirdparty.DialogApp


class ImageDialog : DialogFragment() {
    companion object {

        private const val EXTRA_DIALOG = "dialog"

        fun newInstance(dialogApp: DialogApp): ImageDialog {
            val dialog = ImageDialog()
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments ?: savedInstanceState
        bundle ?: return
        dialogApp = bundle.getSerializable(EXTRA_DIALOG) as DialogApp
        view.findViewById<View>(R.id.btn_close).setOnClickListener { dismiss() }
        dialogApp?.let { dialogInfo ->
            Glide.with(this).load(dialogInfo.banner).into(view.findViewById(R.id.iv_banner))
            Glide.with(this).load(dialogInfo.icon).into(view.findViewById(R.id.iv_icon))
            view.findViewById<TextView>(R.id.tv_title).text = dialogInfo.name
            view.findViewById<TextView>(R.id.tv_content).text = dialogInfo.content
            view.findViewById<View>(R.id.btn_install).setOnClickListener {
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