package com.example.slide.ui.select_image

import android.os.Bundle
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.base.BaseBindingDialog
import com.example.slide.databinding.DialogZoomImageBinding

class ZoomImageDialogFragment : BaseBindingDialog<DialogZoomImageBinding>() {

    override val layoutId: Int
        get() = R.layout.dialog_zoom_image

    override fun bindingView(): DialogZoomImageBinding {
        return DialogZoomImageBinding.inflate(layoutInflater)
    }

    private var urlImage = ""

    companion object {

        const val TAG = "ZoomImageDialogFragment"

        private const val ARG_URL_IMAGE = "url_image"

        fun getInstance(urlImage: String): ZoomImageDialogFragment {
            return ZoomImageDialogFragment().apply {
                arguments = bundleOf(ARG_URL_IMAGE to urlImage)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ARG_URL_IMAGE, urlImage)
    }

    override fun extractData(it: Bundle) {
        super.extractData(it)
        urlImage = it.getString(ARG_URL_IMAGE)!!
    }

    override fun initConfiguration() {
        super.initConfiguration()

        Glide.with(requireActivity()).load(urlImage).into(binding.ivImageDisplay)
    }

    override fun initListener() {
        super.initListener()

        binding.btnRootView.setOnClickListener {
            dismiss()
        }
    }
}