package com.example.slide.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.util.Log
import com.example.slide.ui.my_studio.MyVideo
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.io.File

object VideoTagUtils {


    val VIDEO_ID = MediaStore.Video.Media._ID

    val VIDEO_TITLE = MediaStore.Video.Media.TITLE

    val VIDEO_ARTIST = MediaStore.Video.Media.ARTIST

    val VIDEO_FILEPATH = MediaStore.Video.Media.DATA

    val VIDEO_DURATION = MediaStore.Video.Media.DURATION

    val VIDEO_DATE_ADDED = MediaStore.Video.Media.DATE_ADDED

    val VIDEO_DATE_MODIFIED = MediaStore.Video.Media.DATE_MODIFIED

    val VIDEO_SIZE = MediaStore.Video.Media.SIZE

    val VIDEO_MIME_TYPE = MediaStore.Video.Media.MIME_TYPE

    fun writeVideoTag(context: Context, filePath: String): Long {
        if (Utils.isScopeStorage()) return -1
        val file = File(filePath)
        if (!file.exists()) {
            return -1
        }
        val duration = getDuration(filePath)
        if (duration <= 0) {
            return -1
        }
        val fileName = getFileName(file.name)
        val currentTimeMillis = System.currentTimeMillis() / 1000
        val contentValues = ContentValues()
        contentValues.put(VIDEO_DURATION, duration)
        contentValues.put(VIDEO_TITLE, fileName)
        contentValues.put(VIDEO_DATE_ADDED, currentTimeMillis)
        contentValues.put(VIDEO_DATE_MODIFIED, currentTimeMillis)
        contentValues.put(VIDEO_SIZE, file.length())
        contentValues.put(VIDEO_MIME_TYPE, "video/*")
        contentValues.put(VIDEO_FILEPATH, filePath)
        contentValues.put(VIDEO_ARTIST, MyStatic.SHARK_STUDIO)

        val contentResolver = context.contentResolver
        var query = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            null,
            "_data = ?",
            arrayOf(filePath),
            null
        )
        if (query == null || query.count <= 0) {
            try {
                FirebaseCrashlytics.getInstance()
                    .log("Insert tag for video")
                val insert =
                    contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
                Log.d("kimkaka", "Add media" + insert)
                context.sendBroadcast(Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", insert))
            } catch (e: IllegalArgumentException) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        } else {
            contentResolver.update(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                contentValues,
                "_data = ?",
                arrayOf(filePath)
            )
            Log.d("kimkaka", "Update media")
        }
        query?.close()

        query = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            null,
            "_data = ?",
            arrayOf(filePath),
            null
        )
        if (query != null && query.moveToFirst()) {
            val id: Long = query.getLong(query.getColumnIndex(VIDEO_ID))
            return id
        }

        return -1
    }

    fun updateTag(context: Context, video: MyVideo): Int {
        val contentValues = ContentValues()
        contentValues.put(VIDEO_TITLE, video.name)
        contentValues.put(VIDEO_FILEPATH, video.url)
        return context.contentResolver.update(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            contentValues, "_id = ?", arrayOf(video.id.toString())
        )
    }

    private fun getFileName(str: String): String {
        return str.substring(0, str.lastIndexOf("."))
    }

    private fun getDuration(str: String): Long {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        try {
            mediaMetadataRetriever.setDataSource(str)
            return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLong() ?: 0
        } catch (e: Exception) {
            return 0
        }

    }
}