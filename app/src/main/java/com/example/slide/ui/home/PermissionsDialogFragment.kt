package com.example.slide.ui.home

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.slide.R
import com.example.slide.base.BaseBindingDialog
import com.example.slide.databinding.DialogPermissionBinding
import com.example.slide.util.Utils

class PermissionsDialogFragment : BaseBindingDialog<DialogPermissionBinding>(), View.OnClickListener {

    companion object {

        const val TAG = "PermissionsDialogFragme"

        fun getInstance(): PermissionsDialogFragment {
            return PermissionsDialogFragment()
        }
    }

    override fun bindingView(): DialogPermissionBinding {
        return DialogPermissionBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_permission, container)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOpenSettings.setOnClickListener(this)
        binding.btnQuit.setOnClickListener(this)
        if (Utils.isScopeStorage()) {
            binding.tvDescription2.visibility = View.GONE
        } else {
            binding.tvDescription2.visibility = View.VISIBLE
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnOpenSettings -> {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                dismiss()
                requireContext().startActivity(intent)
            }
            binding.btnQuit -> {
                dismiss()
            }
        }
    }
}