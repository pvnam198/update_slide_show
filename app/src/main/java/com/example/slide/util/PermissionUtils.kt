package com.example.slide.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat

object PermissionUtils {

    const val REQUEST_CODE = 101

    fun requestPermission(activity: Activity) {
        val permissions = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        activity.requestPermissions(
            Array(permissions.size) { permissions[it] }, REQUEST_CODE
        )
    }

    fun isHavePermission(context: Context): Boolean {

        var readMediaImagesPermission = true
        var readMediaAudioPermission = true
        var readMediaVideoPermission = true
        var readExternalStorage = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            readMediaImagesPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED

            readMediaAudioPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            readMediaVideoPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED
        }else{
            readExternalStorage = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
        
        val writeExternalStorage: Boolean =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) true else {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }

        Log.d("hehe1231312", "readMediaImagesPermission: $readMediaImagesPermission")
        Log.d("hehe1231312", "readMediaAudioPermission: $readMediaAudioPermission")
        Log.d("hehe1231312", "readMediaVideoPermission: $readMediaVideoPermission")
        Log.d("hehe1231312", "readExternalStorage: $readExternalStorage")
        Log.d("hehe1231312", "writeExternalStorage: $writeExternalStorage")

        return readMediaImagesPermission
                && readMediaAudioPermission
                && readMediaVideoPermission
                && readExternalStorage
                && writeExternalStorage
    }

}