package com.example.slide.ui.create

import android.app.AlertDialog
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.slide.R
import com.example.slide.ads.InterstitialHelperV2
import com.example.slide.ads.OnInterstitialCallback
import com.example.slide.base.BaseBindingDialog
import com.example.slide.database.entities.Draft
import com.example.slide.databinding.DialogCreateVideoBinding
import com.example.slide.exception.MissingFileException
import com.example.slide.exception.RestoreDraftException
import com.example.slide.repository.DraftRepository
import com.example.slide.ui.my_studio.MyStudioActivity
import com.example.slide.ui.select_image.SelectActivity
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.util.FileUtils
import com.example.slide.util.copyFolder

import timber.log.Timber
import java.io.File

class CreateVideoDialog : BaseBindingDialog<DialogCreateVideoBinding>(), View.OnClickListener {

    override val layoutId: Int
        get() = R.layout.dialog_create_video

    override fun bindingView(): DialogCreateVideoBinding {
        return DialogCreateVideoBinding.inflate(layoutInflater)
    }

    override val isClearFlag = false

    companion object {
        const val TAG = "CreateVideoDialog"

        fun createInstance(): CreateVideoDialog {
            return CreateVideoDialog()
        }
    }

    private lateinit var draftRepository: DraftRepository

    private lateinit var draftAdapter: DraftAdapter

    private var draftCount = 0

    override fun initConfiguration() {
        draftRepository = DraftRepository(requireContext())
        draftAdapter = DraftAdapter(onDraftClicked = {
            try {
                checkDraft(it)
                openActivity(
                    VideoCreateActivity.getIntent(
                        requireContext(),
                        it,
                        VideoCreateActivity.MODE_EDIT_DRAFT
                    )
                )
            } catch (ex: MissingFileException) {
                MissingImageDialog.createInstance(it)
                    .show(childFragmentManager, MissingImageDialog.TAG)
            } catch (ex: RestoreDraftException) {
                deleteDraft(it)
                RestoreDraftErrorDialog().show(childFragmentManager, RestoreDraftErrorDialog.TAG)
            }
        }, onRenameClicked = {
            showRenameDraftDialog(it)
        }, onCopyClicked = {
            duplicateDraft(it)
        }, onDeleteClicked = {
            showDialogDeleteVideo(it)
        })
        binding.rcvDraft.adapter = draftAdapter
    }

    private fun deleteDraft(draft: Draft) {
        lifecycleScope.launchWhenResumed {
            try {
                draftRepository.deleteDraft(draft.id)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.your_draft_has_been_deleted),
                    Toast.LENGTH_SHORT
                ).show()
                fetchDraft()
            } catch (ex: Exception) {
                Timber.d(ex)
            }
        }
    }

    @Throws(RestoreDraftException::class, MissingFileException::class)
    private fun checkDraft(draft: Draft) {
        val originalImageSize = draft.images.size
        val missingImages = draft.images.filter {
            !File(it.url).exists()
        }

        val missingMusics = draft.cropMusic.filter {
            it.track?.url?.let { url ->
                !File(url).exists()
            } == true
        }

        when {
            originalImageSize - missingImages.size < 2 -> throw RestoreDraftException()
            missingImages.isNotEmpty() || missingMusics.isNotEmpty() -> throw MissingFileException()
        }
    }

    private fun duplicateDraft(draft: Draft) {
        lifecycleScope.launchWhenResumed {
            try {
                val originalId = draft.id
                draftRepository.duplicateDraft(originalId)
                fetchDraft()
            } catch (ex: Exception) {
                Timber.d(ex)
            }
        }
    }

    private fun showDialogDeleteVideo(draft: Draft) {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.are_you_sure_to_delete_the_draft))
            .setTitle(R.string.confirm_delete)
            .setPositiveButton(R.string.ok) { dialog, which ->
                deleteDraft(draft)
            }
            .setNegativeButton(R.string.cancel) { dialog, whichButton ->
                dialog.cancel()
            }
            .create()
        dialog.show()
    }

    private fun showRenameDraftDialog(draft: Draft) {
        val view = layoutInflater.inflate(R.layout.dialog_rename_draft, null, false)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle(R.string.rename_draft)
            .setPositiveButton(
                R.string.ok
            ) { dialog, whichButton -> }
            .setNegativeButton(
                R.string.cancel
            ) { dialog, whichButton -> dialog.cancel() }.create()

        val titleEditText = view.edt_name
        titleEditText.setText(draft.title)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                val title = titleEditText.text.toString()
                when {
                    title == draft.title -> {
                        dialog.cancel()
                    }
                    TextUtils.isEmpty(title) -> {
                        Toast.makeText(
                            requireContext(),
                            R.string.please_enter_file_name,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        dialog.cancel()
                    }
                    else -> {
                        lifecycleScope.launchWhenResumed {
                            try {
                                draftRepository.renameDraft(title, draft.id)
                                fetchDraft()
                            } catch (ex: Exception) {
                                Timber.d(ex)
                            } finally {
                                dialog.dismiss()
                            }
                        }
                    }
                }
            }
    }

    override fun initListener() {
        binding.root.setOnClickListener(this)
        binding.btnCreateNewVideo.setOnClickListener(this)
        binding.btnTotalDraft.setOnClickListener {
            openActivity(
                MyStudioActivity.getCallingIntent(
                    requireContext(),
                    MyStudioActivity.DRAFT_MODE
                )
            )
        }
    }

    override fun initTask() {
        fetchDraft()
    }

    private fun fetchDraft() {
        lifecycleScope.launchWhenCreated {
            draftCount = draftRepository.getDraftCount()
            draftAdapter.set(draftRepository.getListDraft(3))
            binding.tvDraftNumber.text = draftCount.toString().plus(" DRAFTS")
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.root -> dismiss()
            binding.btnCreateNewVideo -> {
                openActivity(
                    SelectActivity.getIntent(
                        requireContext(),
                        SelectActivity.MODE_START
                    )
                )
            }
        }
    }

    private fun openActivity(intent: Intent) {
        InterstitialHelperV2.showInterstitialAd(requireActivity(), object : OnInterstitialCallback {
            override fun onWork() {
                dismiss()
                startActivity(intent)
            }

        })
    }

}