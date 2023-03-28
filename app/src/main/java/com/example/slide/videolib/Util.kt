package com.example.slide.videolib

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.AsyncTask
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import java.io.*

object Util {
    fun close(inputStream: InputStream?) {
        if (inputStream != null) {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun close(outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                outputStream.flush()
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    //==============================================================================
    fun isDebug(context: Context): Boolean {
        return context.applicationContext
            .applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    fun convertInputStreamToString(inputStream: InputStream?): String? {
        try {
            val r =
                BufferedReader(InputStreamReader(inputStream))
            var str: String?
            val sb = StringBuilder()
            while (r.readLine().also { str = it } != null) {
                sb.append(str)
            }
            return sb.toString()
        } catch (e: IOException) {
        }
        return null
    }

    fun destroyProcess(process: Process?) {
        if (process != null) {
            try {
                process.destroy()
            } catch (e: Exception) {
            }
        }
    }

    fun killAsync(asyncTask: AsyncTask<*, *, *>?): Boolean {
        return asyncTask != null && !asyncTask.isCancelled && asyncTask.cancel(true)
    }

    fun isProcessCompleted(process: Process?): Boolean {
        try {
            if (process == null) return true
            process.exitValue()
            return true
        } catch (e: IllegalThreadStateException) {
            // do nothing
        }
        return false
    }


    fun createImageView(
        context: Context?,
        paramInt1: Int,
        paramInt2: Int,
        paramInt3: Int,
        paramInt4: Int
    ): ImageView {
        val imageView = ImageView(context)
        val localLayoutParams =
            FrameLayout.LayoutParams(paramInt3, paramInt4)
        localLayoutParams.leftMargin = paramInt1
        localLayoutParams.topMargin = paramInt2
        localLayoutParams.gravity = Gravity.CENTER
        imageView.layoutParams = localLayoutParams
        imageView.scaleType = ImageView.ScaleType.CENTER
        return imageView
    }

    fun getStatusBarHeight(activity: Activity): Int {
        var result = 0
        val resourceId =
            activity.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = activity.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun getRandomIndex(min: Int, max: Int): Int {
        return (Math.random() * (max - min + 1)).toInt() + min
    }

    interface ObservePredicate {
        val isReadyToProceed: Boolean
    }
}