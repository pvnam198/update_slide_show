package com.example.slide.ui.create

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.example.slide.R
import com.example.slide.ads.InterstitialHelperV2
import com.example.slide.ads.OnInterstitialCallback
import com.example.slide.base.BaseDialogFragment
import com.example.slide.database.entities.Draft
import com.example.slide.repository.DraftRepository
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import kotlinx.android.synthetic.main.dialog_missing_image.*
import timber.log.Timber
import java.io.File

class MissingImageDialog : BaseDialogFragment() {

    companion object {

        const val TAG = "MissingImageDialog"

        const val ARG_DRAFT = "DRAFT"

        fun createInstance(draft: Draft): MissingImageDialog {
            return MissingImageDialog().apply {
                arguments = bundleOf(ARG_DRAFT to draft)
            }
        }
    }

    private lateinit var draftRepository: DraftRepository

    private lateinit var draft: Draft

    override val layoutId: Int
        get() = R.layout.dialog_missing_image

    override fun initConfiguration() {
        super.initConfiguration()
        draftRepository = DraftRepository(requireContext())
    }

    override fun extractData(it: Bundle) {
        super.extractData(it)
        draft = it.getParcelable<Draft>(ARG_DRAFT) as Draft
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_cancel.setOnClickListener {
            dismiss()
        }

        btn_ok.setOnClickListener {
            deleteMissingFileFromDraft()
            openActivity(
                    VideoCreateActivity.getIntent(
                            requireContext(),
                            draft,
                            VideoCreateActivity.MODE_EDIT_DRAFT
                    )
            )
        }
    }

    private fun deleteMissingFileFromDraft() {
        lifecycleScope.launchWhenResumed {
            try {
                val missingImages = draft.images.filter {
                    !File(it.url).exists()
                }

                val missingMusics = draft.cropMusic.filter {
                    it.track?.url?.let { url ->
                        !File(url).exists()
                    } == true
                }

                draft.images.removeAll(missingImages)
                draft.cropMusic.removeAll(missingMusics)
                draftRepository.saveAsDraft(draft)
            } catch (ex: Exception) {
                Timber.d(ex)
            }
        }
    }

    private fun openActivity(intent: Intent) {
        InterstitialHelperV2.showInterstitialAd(requireActivity(), object : OnInterstitialCallback {
            override fun onWork() {
                (requireParentFragment() as CreateVideoDialog).dismiss()
                dismiss()
                startActivity(intent)
            }

        })
    }
}