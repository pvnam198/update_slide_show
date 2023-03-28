package com.example.slide.util

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.MediaStore
import android.text.TextUtils
import androidx.core.content.FileProvider
import com.example.slide.R
import com.example.slide.data.SharkVideoDao
import com.example.slide.ui.my_studio.MyVideo
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object VideoUtils {

    fun shareVideo(context: Context, myVideo: MyVideo) {
        try {
            val intent = Intent()
                .setAction(Intent.ACTION_SEND)
                .putExtra(
                    Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(context, context.packageName, File(myVideo.url))
                )
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setType("video/*")
            context.startActivity(
                Intent.createChooser(
                    intent, context.getString(R.string.app_name) + " :"
                            + myVideo.name
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getVideoFromPath(context: Context, path: String): MyVideo? {
        try {
            val contentResolver = context.contentResolver
            val projection = arrayOf(
                MediaStore.Video.Media.DATA,//0
                MediaStore.Video.Media._ID,//1
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,//2
                MediaStore.Video.Media.DURATION,//3
                MediaStore.Video.Media.TITLE,//4
                MediaStore.Video.Media.DATE_TAKEN,//5
                MediaStore.Video.Media.DISPLAY_NAME//6
            )
            val cur = context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, MediaStore.Video.VideoColumns.DATA + " = '$path'",
                null, null
            )
            if (cur?.moveToFirst() == true) {
                val url = cur.getString(0)
                val id = cur.getLong(1)
                val bucketDisplayName = cur.getString(2)
                val duration = cur.getLong(3)
                val name = cur.getString(4)
                val createdDate = cur.getLong(5)
                val displayName = cur.getString(6)
                return MyVideo(id, url, name, duration)
            }
            cur?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}