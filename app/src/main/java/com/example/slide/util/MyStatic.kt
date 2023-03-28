package com.example.slide.util

import android.os.Environment
import com.example.slide.ui.video.video_export.ExportVideoActivity
import java.io.File

object MyStatic {

    const val SHARK_STUDIO = "Shark studio"

    const val VIDEO_FOLDER_NAME = "Shark video"

    val VIDEO_RELATIVE_PATH = Environment.DIRECTORY_MOVIES + "/" + VIDEO_FOLDER_NAME

    @Suppress("DEPRECATION")
    val EXTERNAL_STORAGE_PATH: String = Environment.getExternalStorageDirectory().absolutePath

    val FOLDER_SHARK_VIDEO = if(Utils.isScopeStorage()) "$EXTERNAL_STORAGE_PATH/$VIDEO_RELATIVE_PATH/" else "$EXTERNAL_STORAGE_PATH/$VIDEO_FOLDER_NAME/"

    const val RATE_LATER_TIME_THRESHOLD = 2 * 24 * 60 * 60 * 1000

//    const val ONE_HOUR_IN_MILLIS = 30*1000
    const val ONE_HOUR_IN_MILLIS = 60*60*1000

    val ACCEPT_IMAGE_EXTENSIONS = arrayListOf("png","PNG","jpg","jpeg",
        "bmp","dds","gif","hdr","ico","webp")


    val PREPARE_JOB_ID = 777

    val EXPORT_JOB_ID = 778
}