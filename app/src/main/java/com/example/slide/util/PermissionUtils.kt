package com.example.slide.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionUtils {

    const val REQUEST_CODE = 101

    fun requestPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), REQUEST_CODE
            )
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            activity.requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), REQUEST_CODE
            )
        }
    }

    fun isHavePermission(context: Context): Boolean {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M )
            return true
        val isReadExternal = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) return isReadExternal
        val isWriteExternal = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        return isReadExternal && isWriteExternal
    }

}