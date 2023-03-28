package com.example.slide.ui.my_studio

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.text.TextUtils
import com.example.slide.data.SharkVideoDao
import com.example.slide.util.MyStatic
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object VideoProvider {

    fun getCreatedVideo(context: Context): ArrayList<MyVideo> {
        val videos = ArrayList<MyVideo>()
        val projection = arrayOf(
            MediaStore.Video.Media.DATA,//0
            MediaStore.Video.Media._ID,//1
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,//2
            MediaStore.Video.Media.DURATION,//3
            MediaStore.Video.Media.TITLE,//4
            MediaStore.Video.Media.DATE_TAKEN,//5
            MediaStore.Video.Media.DISPLAY_NAME//6
        )
        val scopeStorageVideoIds = SharkVideoDao.getInstance(context).list

        val cur: Cursor?
        if (scopeStorageVideoIds.isEmpty()) {
            cur = context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, MediaStore.Video.VideoColumns.ARTIST + " = '${MyStatic.SHARK_STUDIO}'",
                null, MediaStore.Video.Media.DATE_TAKEN + " ASC"
            )
        } else {
            val whereId: String = String.format(Locale.US,
                MediaStore.Video.Media._ID + " IN (%s)",
                TextUtils.join(",", Collections.nCopies(scopeStorageVideoIds.size, "?"))
            )
            val size = scopeStorageVideoIds.size + 1
            val arr = Array<String>(size) {
                if (it == 0)
                    MyStatic.SHARK_STUDIO
                else
                    scopeStorageVideoIds[it - 1]
            }
            cur = context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, MediaStore.Video.VideoColumns.ARTIST + " = ? OR " + whereId,
                arr, MediaStore.Video.Media.DATE_TAKEN + " ASC"
            )
        }

        if (cur?.moveToFirst() == true) {
            do {
                val url = cur.getString(0)
                val id = cur.getLong(1)
                val bucketDisplayName = cur.getString(2)
                val duration = cur.getLong(3)
                val name = cur.getString(4)
                val createdDate = cur.getLong(5)
                val displayName = cur.getString(6)
                if (File(url).exists()) {
                    videos.add(MyVideo(id, url, name, duration))
                }
            } while (cur.moveToNext())
        }

        cur?.close()
        return videos
    }
}