package com.example.slide.ui.video.video_share

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.example.slide.R
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.example.slide.data.SharkVideoDao
import com.example.slide.ui.home.MainActivity
import com.example.slide.ui.my_studio.MyVideo
import com.example.slide.util.*
import kotlinx.android.synthetic.main.activity_export_video.*
import kotlinx.android.synthetic.main.activity_video_share.*
import kotlinx.android.synthetic.main.activity_video_share.btn_back
import kotlinx.android.synthetic.main.activity_video_share.btn_delete
import kotlinx.android.synthetic.main.activity_video_share.btn_home
import kotlinx.android.synthetic.main.activity_video_share.btn_rename
import kotlinx.android.synthetic.main.activity_video_share.btn_toggle_video
import kotlinx.android.synthetic.main.activity_video_share.frame_rename
import kotlinx.android.synthetic.main.activity_video_share.iv_toggle_video
import kotlinx.android.synthetic.main.activity_video_share.seek_bar
import kotlinx.android.synthetic.main.activity_video_share.tv_end_time
import kotlinx.android.synthetic.main.activity_video_share.tv_header_title
import kotlinx.android.synthetic.main.activity_video_share.tv_start_time
import kotlinx.android.synthetic.main.activity_video_share.video_view
import kotlinx.android.synthetic.main.dialog_rename_video.view.*
import kotlinx.android.synthetic.main.video_share.*
import java.io.File
import java.util.*

class ShareVideoActivity : BaseActivity(), SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    override fun initViewTools() = InitViewTools({ R.layout.activity_video_share })

    companion object {

        const val EXTRA_VIDEO = "extra_video"

        const val REQUEST_DELETE_FILE = 11

        fun getInstance(context: Context, video: MyVideo): Intent {
            val intent = Intent(context, ShareVideoActivity::class.java)
            intent.putExtra(EXTRA_VIDEO, video)
            return intent
        }
    }

    private lateinit var video: MyVideo

    private val timer = Timer()

    private val tickRunnable = object : TimerTask() {
        override fun run() {
            runOnUiThread {
                seek_bar.progress = video_view.currentPosition
                tv_start_time.text =
                    StringUtils.getDurationDisplayFromMillis(video_view.currentPosition)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(EXTRA_VIDEO, video)
    }

    override fun extractData(bundle: Bundle) {
        super.extractData(bundle)
        video = bundle.getSerializable(EXTRA_VIDEO) as MyVideo
    }

    override fun initConfiguration(savedInstanceState: Bundle?) {
        super.initConfiguration(savedInstanceState)
        if (Utils.isScopeStorage()) {
            frame_rename.visibility = View.GONE
        } else {
            frame_rename.visibility = View.VISIBLE
        }
        video_view.setVideoPath(video.url)
        btn_rename.visibility = if (Utils.isScopeStorage()) View.GONE else View.VISIBLE
        tv_header_title.text = video.name
    }

    override fun initListener() {
        super.initListener()

        btn_back.setOnClickListener(this)
        btn_home.setOnClickListener(this)
        btn_toggle_video.setOnClickListener(this)
        btn_rename.setOnClickListener(this)
        btn_delete.setOnClickListener(this)
        btn_twitter.setOnClickListener(this)
        btn_instagram.setOnClickListener(this)
        btn_facebook.setOnClickListener(this)
        btn_youtube.setOnClickListener(this)
        btn_share_more.setOnClickListener(this)
        seek_bar.setOnSeekBarChangeListener(this)

        video_view.setOnPreparedListener {
            seek_bar.max = it.duration
            tv_end_time.text = StringUtils.getDurationDisplayFromMillis(it.duration)
            video_view.start()
            iv_toggle_video.setImageResource(R.drawable.ic_pause)
        }
        video_view.setOnCompletionListener {
            iv_toggle_video.setImageResource(R.drawable.ic_play)
        }
    }

    private fun showDialogRename() {
        val view = layoutInflater.inflate(R.layout.dialog_rename_video, null, false)
        val dialog = AlertDialog.Builder(this)
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
                    Toast.makeText(this, R.string.please_enter_file_name, Toast.LENGTH_SHORT).show()
                    dialog.cancel()
                } else {
                    var url = video.url
                    if (url.lastIndexOf("/") != -1) {
                        url = url.substring(0, url.lastIndexOf("/") + 1)
                        url = "$url$title.mp4"
                        val file = File(url)
                        if (file.exists()) {
                            Toast.makeText(
                                this,
                                R.string.file_name_is_already_exists,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val success = File(video.url).renameTo(File(url))
                            if (success) {
                                Toast.makeText(
                                    this,
                                    R.string.file_successfully_renamed,
                                    Toast.LENGTH_SHORT
                                ).show()
                                video.url = url
                                video.name = title
                                VideoTagUtils.updateTag(this, video)
                                tv_header_title.text = video.name
                            } else {
                                Toast.makeText(
                                    this,
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

    private fun showDialogDeleteVideo() {
        val dialog = AlertDialog.Builder(this)
            .setMessage(R.string.msg_delete_video)
            .setTitle(R.string.confirm_delete)
            .setPositiveButton(R.string.ok) { dialog, which ->
                val success = FileUtils.deleteVideoFromDevice(this, video.id)
                if (success) {
                    Toast.makeText(this, R.string.video_deleted, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, R.string.msg_cant_delete, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, whichButton ->
                dialog.cancel()
            }
            .create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_DELETE_FILE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, R.string.video_deleted, Toast.LENGTH_SHORT).show()
            SharkVideoDao.getInstance(this).deleteByVideoId(video.id)
            finish()
        }
    }

    private fun deleteVideo() {
        if (Utils.isScopeStorage()) {
            val uris = ArrayList<Uri>()
            uris.add(
                ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    video.id
                )
            )
            MediaStore.createDeleteRequest(contentResolver, uris)
            val pendingIntent =
                MediaStore.createDeleteRequest(contentResolver, uris.filter {
                    checkUriPermission(
                        it,
                        Binder.getCallingPid(),
                        Binder.getCallingUid(),
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    ) != PackageManager.PERMISSION_GRANTED
                })
            startIntentSenderForResult(
                pendingIntent.intentSender,
                ShareVideoActivity.REQUEST_DELETE_FILE,
                null,
                0,
                0,
                0
            )
        } else {
            showDialogDeleteVideo()
        }
    }

    override fun releaseData() {
        timer.cancel()
        video_view.stopPlayback()
    }

    override fun initTask() {
        super.initTask()
        timer.scheduleAtFixedRate(tickRunnable, 300, 300)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            video_view.seekTo(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_back -> {
                onBackPressed()
            }
            R.id.btn_home -> {
                startActivity(MainActivity.getCallingIntent(this))
            }
            R.id.btn_toggle_video -> {
                if (video_view.isPlaying) {
                    iv_toggle_video.setImageResource(R.drawable.ic_play)
                    video_view.pause()
                } else {
                    iv_toggle_video.setImageResource(R.drawable.ic_pause)
                    video_view.start()
                }
            }
            R.id.btn_rename -> {
                showDialogRename()
            }
            R.id.btn_delete -> {
                deleteVideo()
            }
            R.id.btn_twitter -> {
                ShareUtils.createTwitterIntent(this, File(video.url))
            }
            R.id.btn_instagram -> {
                ShareUtils.createInstagramIntent(this, File(video.url))
            }
            R.id.btn_facebook -> {
                ShareUtils.createFacebookIntent(this, File(video.url))
            }
            R.id.btn_youtube -> {
                ShareUtils.createYoutubeIntent(this, File(video.url))
            }
            R.id.btn_share_more -> {
                ShareUtils.createShareMoreIntent(this, File(video.url))
            }
        }
    }

}