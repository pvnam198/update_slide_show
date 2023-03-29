package com.example.slide.ui.my_studio

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.database.entities.Draft
import com.example.slide.databinding.FragmentDraftBinding
import com.example.slide.repository.DraftRepository
import com.example.slide.ui.select_image.SelectActivity
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import timber.log.Timber

class DraftFragment : BaseFragment<FragmentDraftBinding>() {

    private lateinit var draftRepository: DraftRepository

    private lateinit var draftAdapter: DraftAdapter

    override fun initViewTools() = InitViewTools({
        R.layout.fragment_draft
    }, { false })

    override fun releaseData() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        draftRepository = DraftRepository(requireContext())
        draftAdapter = DraftAdapter(
            onDraftClicked = {
                startActivity(
                    VideoCreateActivity.getIntent(
                        requireContext(),
                        it,
                        VideoCreateActivity.MODE_EDIT_DRAFT
                    )
                )
            }, onRenameClicked = {
                showRenameDraftDialog(it)
            }, onCopyClicked = {
                duplicateDraft(it)
            }, onDeleteClicked = {
                showDialogDeleteVideo(it)
            })
    }

    override fun onResume() {
        super.onResume()
        fetchDraft()
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

        val titleEditText = view.findViewById<EditText>(R.id.edt_name)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rcvDraft.adapter = draftAdapter

        binding.btnCreateVideo.setOnClickListener {
            startActivity(SelectActivity.getIntent(requireContext(), SelectActivity.MODE_START))
        }
    }

    override fun bindingView(): FragmentDraftBinding {
        return FragmentDraftBinding.inflate(layoutInflater)
    }

    private fun fetchDraft() {
        lifecycleScope.launchWhenCreated {
            val listDraft = draftRepository.getAllDraft()
            if (listDraft.isEmpty()) {
                binding.layoutNoDraft.visibility = View.VISIBLE
            } else {
                binding.layoutNoDraft.visibility = View.GONE
            }
            draftAdapter.set(listDraft)
        }
    }
}