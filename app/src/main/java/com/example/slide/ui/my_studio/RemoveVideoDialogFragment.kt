package com.example.slide.ui.my_studio

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.slide.R
import com.example.slide.util.FileUtils
import kotlinx.android.synthetic.main.dialog_remove_video.*
import org.greenrobot.eventbus.EventBus

class RemoveVideoDialogFragment :
    DialogFragment(),
    View.OnClickListener {

    companion object {

        private lateinit var video: MyVideo

        const val TAG = "RemoveVideoDialogFragment"

        fun getInstance(video: MyVideo): RemoveVideoDialogFragment {
            this.video = video
            return RemoveVideoDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_remove_video, container)
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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_no.setOnClickListener(this)
        btn_yes.setOnClickListener(this)

        tv_header.text = requireContext().getString(R.string.delete_video) + video.name
    }

    override fun onClick(view: View) {
        when (view) {
            btn_no -> {
                dismiss()
            }
            btn_yes -> {
                dismiss()
                val success = FileUtils.deleteVideoFromDevice(requireContext(), video.id)
                if (success) {
                    Toast.makeText(requireContext(), R.string.video_deleted, Toast.LENGTH_SHORT)
                        .show()
                    EventBus.getDefault().post(VideoDeletedEvent(video))
                } else {
                    Toast.makeText(requireContext(), R.string.msg_cant_delete, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}