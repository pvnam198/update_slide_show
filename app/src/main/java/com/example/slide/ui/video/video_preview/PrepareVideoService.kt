package com.example.slide.ui.video.video_preview

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import androidx.core.app.JobIntentService
import com.example.slide.AnimateImage
import com.example.slide.MyApplication
import com.example.slide.database.entities.Draft
import com.example.slide.ui.video.video_preview.model.DataPreview
import com.example.slide.util.BitmapUtil
import com.example.slide.util.FileUtils
import com.example.slide.util.MyStatic
import com.example.slide.videolib.NewMaskBitmap
import com.example.slide.videolib.VideoConfig
import timber.log.Timber
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

class PrepareVideoService : JobIntentService() {

    private lateinit var dataPreview: DataPreview

    private lateinit var draft: Draft

    private val urlImages = ArrayList<String>()

    private var videoQuality = VideoConfig.VIDEO_QUALITY_480

    private val myApplication by lazy { application as MyApplication }

    companion object {

        private const val EXTRA_DATA_PREVIEW = "data_preview"

        private const val EXTRA_DRAFT = "draft"

        private const val EXTRA_JOB_ID = "data_id"

        private var current_job_id = -1L

        fun enqueueCreateImage(
            context: Context,
            data: DataPreview,
            draft: Draft,
            videoQuality: Int = VideoConfig.VIDEO_QUALITY_480
        ) {
            current_job_id = System.nanoTime()
            val intent = Intent(context, PrepareVideoService::class.java)
            intent.putExtra(EXTRA_DATA_PREVIEW, data)
            intent.putExtra(EXTRA_JOB_ID, current_job_id)
            intent.putExtra(EXTRA_DRAFT, draft)
            intent.putExtra(VideoConfig.VIDEO_QUALITY, videoQuality)
            enqueueWork(context, PrepareVideoService::class.java, MyStatic.PREPARE_JOB_ID, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {

        val runtime = Runtime.getRuntime()
        val usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L
        val maxHeapSizeInMB = runtime.maxMemory() / 1048576L
        val availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB
        Log.d("kimkakaservice2", "runtime.totalMemory()" + runtime.totalMemory() / 1048576L)
        Log.d("kimkakaservice2", "usedMemInMB" + usedMemInMB)
        Log.d("kimkakaservice2", "maxHeapSizeInMB" + maxHeapSizeInMB)
        Log.d("kimkakaservice2", "availHeapSizeInMB" + availHeapSizeInMB)

        dataPreview = intent.getSerializableExtra(EXTRA_DATA_PREVIEW) as DataPreview
        draft = intent.getParcelableExtra<Draft>(EXTRA_DRAFT) as Draft
        val jobId = intent.getLongExtra(EXTRA_JOB_ID, -1L)
        urlImages.clear()
        for (image in dataPreview.images) {
            urlImages.add(image.url)
        }
        myApplication.videoDataState.initWithImageNumber(urlImages.size)
        Log.d("kimkakaservice2", "start create image")
        videoQuality = intent.getIntExtra(VideoConfig.VIDEO_QUALITY, VideoConfig.VIDEO_QUALITY_480)
        myApplication.videoDataState.setIsPreparing(true,videoQuality)
        VideoConfig.setupVideoQuality(videoQuality)
        createImage(jobId)
    }

    private fun createImage(jobId: Long) {
        var bitmapEnd: Bitmap? = null
        var i = 0
        val markPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val videoWidth = VideoConfig.getVideoWidth()
        val videoHeight = VideoConfig.getVideoHeight()

        Timber.d("createImage width: $videoWidth")
        Timber.d("createImage videoHeight: $videoHeight")

        markPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        while (i < dataPreview.images.size - 1) {
            Log.d("kimkakasevice", "start 1 create")
            var bitmapStart: Bitmap? = null
            if (i == 0) {
                bitmapStart =
                    BitmapUtil.loadFullBitmapForVideo(urlImages[i], videoWidth, videoHeight, this)
            } else {
                Log.d("kimkakaservice", "start case 2")
                if (bitmapEnd == null || bitmapEnd.isRecycled) {
                    bitmapEnd = BitmapUtil.loadFullBitmapForVideo(
                        urlImages[i],
                        videoWidth,
                        videoHeight,
                        this
                    )
                }
                if (bitmapEnd != null) {
                    bitmapStart = bitmapEnd
                }
            }
            bitmapEnd =
                BitmapUtil.loadFullBitmapForVideo(urlImages[i + 1], videoWidth, videoHeight, this)

            NewMaskBitmap.reInitRect()
            val effect: NewMaskBitmap.EFFECT =
                dataPreview.selectedTheme.getEffectByPos(i)
            effect.initConfig(bitmapStart, bitmapEnd, videoWidth, videoHeight)
            Log.d("kimkakasevice", "start 2 create")
            Log.d(
                "kimkakasevice",
                "VideoConfig.INSTANCE.getAnimatedFrameSub()" + VideoConfig.animatedFrameSub
            )
            for (j in 0 until VideoConfig.animatedFrame) {
                Log.d("kimkakasevice", "j 2 create" + j)
                val combineBitmap = effect.combineBitmap(
                    this,
                    bitmapStart,
                    bitmapEnd,
                    videoWidth,
                    videoHeight,
                    j
                )
                //temp
                if (current_job_id != jobId || (myApplication.videoDataState.isCancel && videoQuality != VideoConfig.VIDEO_QUALITY_480)) {
                    //xoa temp
                    return
                }
                saveImageToStorage(jobId, combineBitmap, i, j)
            }
            Log.d("kimkakasevice", "end 2 create")
            i++
        }
        myApplication.videoDataState.setIsPreparing(false,videoQuality)
        when (videoQuality) {
            VideoConfig.VIDEO_QUALITY_480 -> myApplication.videoDataState.isDonePrepareImages480 =
                true
            VideoConfig.VIDEO_QUALITY_720 -> myApplication.videoDataState.isDonePrepareImages720 =
                true
            VideoConfig.VIDEO_QUALITY_1080 -> myApplication.videoDataState.isDonePrepareImages1080 =
                true
        }
    }

    private fun saveImageToStorage(jobId: Long, bitmap: Bitmap, animateNum: Int, imageNumber: Int) {
        //               TODO: if (isSameTheme()) {
        val imgDir: File = FileUtils.getPrivatePreviewImageDirectory(
            this,
            dataPreview.selectedTheme.toString(),
            animateNum
        )
        val file2 = File(
            imgDir,
            String.format(Locale.US, "img%02d.jpg", imageNumber)
        )
        try {
            if (file2.exists()) {
                file2.delete()
            }
            val fileOutputStream: OutputStream = FileOutputStream(file2)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e2: IOException) {
            e2.printStackTrace()
        }
        var pos = AnimateImage.NORMAL
        if (current_job_id != jobId) return
        if (imageNumber == VideoConfig.animatedFrameSub) pos = AnimateImage.LAST
        if (imageNumber == 0) pos = AnimateImage.FIRST
        myApplication.videoDataState.addImage(file2.absolutePath, pos)
    }

}