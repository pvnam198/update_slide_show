package com.example.slide.ui.video.video_export

import Jni.FFmpegCmd
import Jni.OnEditorListener
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.JobIntentService
import com.example.slide.MyApplication
import com.example.slide.data.SharkVideoDao
import com.example.slide.event.BeginSavingVideoEvent
import com.example.slide.event.ExportErrorEvent
import com.example.slide.event.VideoCreatedEvent
import com.example.slide.framework.texttovideo.VideoTextExport
import com.example.slide.ui.select_music.model.Track
import com.example.slide.ui.video.video_preview.model.DataPreview
import com.example.slide.util.*
import com.example.slide.videolib.VideoConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

class SavingVideoService : JobIntentService() {

    private lateinit var dataPreview: DataPreview

    private val myApplication by lazy { application as MyApplication }

    companion object {

        private const val EXTRA_JOB_ID = "extra_id"

        private const val EXTRA_DATA_PREVIEW = "extra_preview"

        private var current_job_id = -1L

        fun enqueueSaveVideo(
            context: Context,
            dataPreview: DataPreview,
            videoQuality: Int
        ) {
            current_job_id = System.nanoTime()
            val intent = Intent(context, SavingVideoService::class.java)
            intent.putExtra(EXTRA_DATA_PREVIEW, dataPreview)
            intent.putExtra(EXTRA_JOB_ID, current_job_id)
            intent.putExtra(VideoConfig.VIDEO_QUALITY, videoQuality)
            enqueueWork(context, SavingVideoService::class.java, MyStatic.EXPORT_JOB_ID, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        val jobId = intent.getLongExtra(EXTRA_JOB_ID, -1L)
        dataPreview = intent.getSerializableExtra(EXTRA_DATA_PREVIEW) as DataPreview
        val videoQuality = intent.getIntExtra(
            VideoConfig.VIDEO_QUALITY,
            VideoConfig.VIDEO_QUALITY_480
        )
        myApplication.videoDataState.isExportingVideo = true

        while (true) {
            if (jobId != current_job_id) return
            if (myApplication.videoDataState.isPreparingDone(videoQuality)) break
            if (myApplication.videoDataState.isCancel) return
        }
        myApplication.videoDataState.goSaveVideoMode()
        try {
            Log.d("kimkakaexport", "onHandleWork: tao config file")
            FirebaseCrashlytics.getInstance()
                .log("tao config file")
            if (jobId != current_job_id) return
            createConfigFile(jobId)
            if (jobId != current_job_id) return
            FirebaseCrashlytics.getInstance()
                .log("tao video")
            Log.d("kimkakaexport", "onHandleWork: tao video")
            mereAudioAndCreateVideo(jobId)
            if (jobId != current_job_id) return
        } catch (e: Exception) {
            Log.d("kimkakaexport", "loi" + e.message)
            Log.d("kimkakaexport", "fail try catch export")
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
            EventBus.getDefault().post(ExportErrorEvent())
        }
    }

    private fun mereAudioAndCreateVideo(jobId: Long) {
        myApplication.videoDataState.isCancel = false
        val tracks = getListMusic()
        Log.d("kimkakaexport", "mereAudioAndCreateVideo: ${tracks.size}")
        if (tracks.isEmpty()) {
            createVideo(jobId)
        } else if (tracks.size == 1) {
            createVideo(jobId, tracks[0].url)
        } else {
            var totalDuration = myApplication.videoDataState.totalSecond * 1000
            var count = 1
            tracks.forEachIndexed { index, track ->
                val duration = track.duration
                totalDuration -= duration
                if (totalDuration > 0 && index != tracks.size - 1) count++
            }
            if (count == 1) {
                createVideo(jobId, tracks[0].url)
            } else {
                val output = FileUtils.getAudioMergeFile(this)
                if (output.exists()) output.delete()

                /*docs: https://trac.ffmpeg.org/wiki/Concatenate#differentcodec*/
                val mergeAudios = ArrayList<String>()
                mergeAudios.add("ffmpeg")
                tracks.forEachIndexed { index, track ->
                    mergeAudios.add("-i")
                    mergeAudios.add(track.url)
                }
                mergeAudios.add("-filter_complex")
                val audioBuilder = StringBuilder()
                tracks.forEachIndexed { index, track ->
                    audioBuilder.append("[$index:a]")
                }
                audioBuilder.append("concat=n=${tracks.size}:v=0:a=1[a]")
                mergeAudios.add(audioBuilder.toString())
                mergeAudios.add("-map")
                mergeAudios.add("[a]")
                mergeAudios.add(output.absolutePath)

                /*Log câu lệnh ffmpeg gộp nhạc*/
                FirebaseCrashlytics.getInstance().setCustomKey("cmdMergeAudio", "$mergeAudios")

                val arr = Array(mergeAudios.size) { i -> mergeAudios[i] }

                FFmpegCmd.exec(arr, object : OnEditorListener {
                    override fun onSuccess(): Boolean {
                        if (jobId == current_job_id) createVideo(jobId, output.absolutePath)
                        return false
                    }

                    override fun onFailure() {
//                        FirebaseAnalytics.getInstance(this@SavingVideoService).logEvent("fail_merge_music", null)
                        Log.d("kimkakaexport", "fail merge music")
                        val e = RuntimeException("fail merge music")
                        tracks.forEach { it.updateExists() }
                        FirebaseCrashlytics.getInstance()
                            .setCustomKey("isAllmusicExtists", "$tracks")
                        FirebaseCrashlytics.getInstance().recordException(e)
                        EventBus.getDefault().post(ExportErrorEvent())
                        myApplication.videoDataState.isExportingVideo = false
                    }

                    override fun onProgress(progress: Float) {
                    }
                }, 100)
            }
        }
    }

    private fun getListMusic(): ArrayList<Track> {
        val cropMusics = dataPreview.cropMusics
        Log.d("kimkakaexport"," ${dataPreview.cropMusics.size}")
        val tracks = ArrayList<Track>()
        cropMusics.forEach {
            if (it.defaultMusic != null) {
                Timber.d("aaaaaaaaa")
                val input = assets.open(it.defaultMusic.url)
                val folder = FileUtils.getMusicDefaultDir(this)
                val outFile = File(folder, getString(it.defaultMusic.nameRes) + ".ogg")
                if (!outFile.exists()) {
                    val out = FileOutputStream(outFile)
                    val buffer = ByteArray(1024)
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        out.write(buffer, 0, read)
                    }
                }
                val track = Track(System.currentTimeMillis().toInt(), outFile.absolutePath)
                track.duration = it.defaultMusic.duration
                tracks.add(track)
            } else if (it.track != null) {
                tracks.add(it.track)
            }
        }
        return tracks
    }

    private fun createVideo(jobId: Long, musicUrl: String? = null) {

        val frames = ArrayList<VideoTextExport>()
        val fileName = Utils.getUniqueVideoFileName() + ".mp4"
        EventBus.getDefault().post(BeginSavingVideoEvent(fileName))
        val outputPath = MyStatic.FOLDER_SHARK_VIDEO + fileName
        val ffmpegOutputPath =
            if (Utils.isScopeStorage()) filesDir.absolutePath + "/" + FileUtils.VIDEO_FILE_NAME else outputPath
        if (dataPreview.videoFrame != null) {
            FileUtils.saveFrameToInternalStorage(this, dataPreview.videoFrame!!)
            frames.add(VideoTextExport(FileUtils.getFrameFile(this).absolutePath, 0, 0))
        }
        frames.addAll(dataPreview.texts)

        frames.forEach {
            it.checkExits()
        }
        FirebaseCrashlytics.getInstance().setCustomKey("frames export", "$frames")

        val inputList = ArrayList<String>()
        inputList.add("ffmpeg")
        inputList.add("-y")
        inputList.add("-r")
        inputList.add(VideoConfig.getFps().toString())
        inputList.add("-f")
        inputList.add("concat")
        inputList.add("-safe")
        inputList.add("0")
        inputList.add("-i")
        inputList.add(FileUtils.getVideoConfigFile(this).absolutePath)
        var lastSignal = ""
        if (frames.size == 1) {
            inputList.add("-i")
            inputList.add(frames[0].url)
            inputList.add("-filter_complex")
            var filter = String.format(Locale.US, "overlay= %d:%d", frames[0].x, frames[0].y)
            if (!frames[0].isFullTime) filter += String.format(
                Locale.US,
                ":enable=\'between(t,%d,%d)\'",
                frames[0].start,
                frames[0].end
            )
            inputList.add(filter)
        } else if (frames.isNotEmpty()) {
            lastSignal = "[v1]"
            frames.forEach {
                inputList.add("-i")
                inputList.add(it.url)
            }
            inputList.add("-filter_complex")
            var filter = ""
            frames.forEachIndexed { index, frame ->
                if (index == 0) {
                    filter = String.format(Locale.US, "[0][1]overlay= %d:%d", frame.x, frame.y)
                    if (!frame.isFullTime) filter += String.format(
                        Locale.US,
                        ":enable=\'between(t,%d,%d)\'", frame.start, frame.end
                    )
                    filter += "[v1];"
                } else {
                    filter += String.format(
                        Locale.US,
                        "$lastSignal[${index + 1}]overlay= %d:%d", frame.x, frame.y
                    )
                    if (!frame.isFullTime) filter += String.format(
                        Locale.US,
                        ":enable=\'between(t,%d,%d)\'", frame.start, frame.end
                    )
                    filter += "[v${index + 1}]"
                    if (index != frames.size - 1) filter += ";"
                }
                lastSignal = "[v${index + 1}]"
            }
            inputList.add(filter)
        }

        if (musicUrl != null) {
            inputList.add("-stream_loop")
            inputList.add("-1")
            inputList.add("-i")
            inputList.add(musicUrl)
            if (lastSignal.isNotEmpty()) {
                inputList.add("-map")
                inputList.add("" + (1 + frames.size))
            }
        }

        if (lastSignal.isNotEmpty()) {
            inputList.add("-map")
            inputList.add(lastSignal)
        }

        inputList.add("-strict")
        inputList.add("experimental")
        inputList.add("-r")
        inputList.add(VideoConfig.getFps().toString())
        inputList.add("-t")
        inputList.add(myApplication.videoDataState.totalSecond.toString())
        inputList.add("-c:v")
        inputList.add("libx264")
        inputList.add("-preset")
        inputList.add("ultrafast")
        inputList.add("-pix_fmt")
        inputList.add("yuv420p")
        inputList.add("-ac")
        inputList.add("2")
        inputList.add(ffmpegOutputPath)
//        /data/user/0/com.isaidamier.kotlin.trivialdrive/files/frame_dir/1601967392842.PNG
//        overlay= 0:0:enable=\'between(t,0,6)\'

        /*Log câu lệnh ffmpeg gộp anh*/
        FirebaseCrashlytics.getInstance().setCustomKey("cmdCreateVideo", "$inputList")

        val inputCode = Array(inputList.size) {
            inputList[it]
        }
        createVideo(inputCode, outputPath, fileName, musicUrl)
    }

    private fun createVideo(
        inputCode: Array<String>,
        outputPath: String,
        fileName: String,
        musicUrl: String? = null
    ) {
        FFmpegCmd.exec(
            inputCode,
            object : OnEditorListener {
                override fun onSuccess(): Boolean {
                    var path = outputPath
                    if (Utils.isScopeStorage()) {
                        path = transferToSDCard(fileName)
                    } else {
                        VideoTagUtils.writeVideoTag(applicationContext, outputPath)
                    }
                    val video = VideoUtils.getVideoFromPath(this@SavingVideoService, path)
                    video?.let {
                        EventBus.getDefault().post(VideoCreatedEvent(it))
                    }
                    myApplication.videoDataState.isExportingVideo = false
                    return true
                }

                override fun onFailure() {
                    musicUrl?.let {
                        FirebaseCrashlytics.getInstance()
                            .setCustomKey("music export", "$it:exist: ${File(it).exists()}")
                    }
                    Log.d("kimkakaexport", "fail create video")
                    val e = RuntimeException("fail create video")
                    FirebaseCrashlytics.getInstance().recordException(e)
                    EventBus.getDefault().post(ExportErrorEvent())
                    myApplication.videoDataState.isExportingVideo = false
                }

                override fun onProgress(progress: Float) {
                    myApplication.videoDataState.updateProgress(progress)
                }
            }, myApplication.videoDataState.totalSecond
        )
    }

    private fun transferToSDCard(fileName: String): String {
        val outputPath = MyStatic.FOLDER_SHARK_VIDEO + fileName
        val inputPath = filesDir.absolutePath + "/" + FileUtils.VIDEO_FILE_NAME

        val videoUri = MediaStore.Video.Media
            .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val videoDetails = ContentValues()
        videoDetails.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
        videoDetails.put(MediaStore.Audio.Media.RELATIVE_PATH, MyStatic.VIDEO_RELATIVE_PATH)
        videoDetails.put(MediaStore.Audio.Media.IS_PENDING, 1)
        videoDetails.put(MediaStore.Audio.Media.ARTIST, MyStatic.SHARK_STUDIO)
        val videoContentUri: Uri? = contentResolver.insert(videoUri, videoDetails)
        if (videoContentUri != null && videoContentUri.lastPathSegment != null) {
            val id = videoContentUri.lastPathSegment!!.toLong()
            val outputStream: OutputStream? = contentResolver.openOutputStream(videoContentUri)
            val input = File(inputPath)
            val fileInputStream = FileInputStream(input)
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (fileInputStream.read(buffer).also { bytesRead = it } > 0) {
                outputStream?.write(buffer, 0, bytesRead)
            }
            SharkVideoDao.getInstance(this).create(id)
            videoDetails.clear()
            videoDetails.put(MediaStore.Audio.Media.IS_PENDING, 0)
            contentResolver.update(videoContentUri, videoDetails, null, null)
            //TODO DELETE TEMP VIDEO FOLDER
            File(inputPath).delete()
        } else {
            //TODO POST ERROR EVENT
        }
        return outputPath
    }

    private fun createConfigFile(jobId: Long) {
        val images = myApplication.videoDataState.outputImages

        FirebaseCrashlytics.getInstance().setCustomKey("allimages", "$images")
        val configFile = FileUtils.getVideoConfigFile(this)
        configFile.delete()
        configFile.createNewFile()
        var log = ""
        val buf =
            BufferedWriter(FileWriter(configFile, true))
        if (myApplication.videoDataState.isCancel) return
        images.forEachIndexed { index, url ->
            if (myApplication.videoDataState.isCancel) return
            buf.append(String.format(Locale.US, "file '%s'", url))
            buf.newLine()
            log += ("file $index exists: " + File(url).exists()+" . ")
        }

        FirebaseCrashlytics.getInstance().setCustomKey("allimage",log)

        buf.close()
    }
}