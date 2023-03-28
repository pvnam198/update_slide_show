package com.example.slide.ui.my_studio

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.data.SharkVideoDao
import com.example.slide.ui.select_image.SelectActivity
import com.example.slide.ui.video.video_share.ShareVideoActivity
import com.example.slide.util.FileUtils
import com.example.slide.util.Utils
import com.example.slide.util.VideoTagUtils
import kotlinx.android.synthetic.main.dialog_rename_video.view.*
import kotlinx.android.synthetic.main.fragment_video.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*

class VideoFragment : BaseFragment() {

    private lateinit var adapter: VideoAdapter

    private var tempVideo: MyVideo? = null

    override fun initViewTools() = InitViewTools({
        R.layout.fragment_video
    }, { true })

    override fun releaseData() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = VideoAdapter(
            onVideoClicked = {
                handleClickedVideo(it)
            }, onRenameClicked = {
                showDialogRename(it)
            },
            onDeleteVideoClicked = {
                deleteVideo(it)
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateVideoData()
        rv_videos.layoutManager =
            GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
        rv_videos.adapter = adapter

        btn_create_video.setOnClickListener {
            startActivity(SelectActivity.getIntent(requireContext(), SelectActivity.MODE_START))
        }
    }

    private fun updateVideoData() {
        val videos = VideoProvider.getCreatedVideo(requireContext())
        if (videos.isEmpty()) {
            layout_no_videos.visibility = View.VISIBLE
        } else {
            layout_no_videos.visibility = View.GONE
        }
        adapter.updateData(videos)
    }

    private fun handleClickedVideo(myVideo: MyVideo) {
        startActivity(ShareVideoActivity.getInstance(requireContext(), myVideo))
    }

    fun deleteVideo(video: MyVideo) {
        if (Utils.isScopeStorage()) {
            tempVideo = video
            val uris = ArrayList<Uri>()
            uris.add(
                ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    video.id
                )
            )
            MediaStore.createDeleteRequest(requireContext().contentResolver, uris)
            val pendingIntent =
                MediaStore.createDeleteRequest(requireContext().contentResolver, uris.filter {
                    requireContext().checkUriPermission(
                        it,
                        Binder.getCallingPid(),
                        Binder.getCallingUid(),
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    ) != PackageManager.PERMISSION_GRANTED
                })
            startIntentSenderForResult(
                pendingIntent.intentSender,
                MyStudioActivity.REQUEST_DELETE_FILE,
                null,
                0,
                0,
                0,
                null
            )
        } else {
            RemoveVideoDialogFragment.getInstance(video).show(
                childFragmentManager,
                RemoveVideoDialogFragment.TAG
            )
        }
    }

    fun showDialogRename(video: MyVideo) {
        val view = layoutInflater.inflate(R.layout.dialog_rename_video, null, false)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle(R.string.rename_video)
            .setPositiveButton(
                R.string.ok
            ) { dialog, whichButton -> }
            .setNegativeButton(
                R.string.cancel
            ) { dialog, whichButton -> dialog.cancel() }.create()

        val titleEditText = view.edt_name
        titleEditText.setText(video.name)

        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                val title = titleEditText.text.toString()
                if (title == video.name) {
                    dialog.cancel()
                } else if (TextUtils.isEmpty(title)) {
                    Toast.makeText(
                        requireContext(),
                        R.string.please_enter_file_name,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    dialog.cancel()
                } else {
                    var url = video.url
                    if (url.lastIndexOf("/") != -1) {
                        url = url.substring(0, url.lastIndexOf("/") + 1)
                        url = "$url$title.mp3"
                        val file = File(url)
                        if (file.exists()) {
                            Toast.makeText(
                                requireContext(),
                                R.string.file_name_is_already_exists,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val success = File(video.url).renameTo(File(url))
                            if (success) {
                                Toast.makeText(
                                    requireContext(),
                                    R.string.file_successfully_renamed,
                                    Toast.LENGTH_SHORT
                                ).show()
                                video.url = url
                                video.name = title
                                VideoTagUtils.updateTag(requireContext(), video)
                                updateVideoData()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    R.string.file_not_successfully_renamed,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            dialog.cancel()
                        }
                    }
                }
            }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onVideoEvent(event: VideoEvent) {
        when (event) {
            is VideoDeletedEvent -> updateVideoData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MyStudioActivity.REQUEST_DELETE_FILE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(requireContext(), R.string.video_deleted, Toast.LENGTH_SHORT).show()
            updateVideoData()
        }
    }

}