package com.example.slide.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.example.slide.BuildConfig
import java.io.File


object ShareUtils {

    private const val FILE_PROVIDER = ".fileprovider"

    private const val INSTAGRAM_PACKAGE = "com.instagram.android"

    private const val FACEBOOK_PACKAGE = "com.facebook.katana"

    private const val YOUTUBE_PACKAGE = "com.google.android.youtube"

    private const val TWITTER_PACKAGE = "com.twitter.android"

    private const val SHARE_VIA = "share_via"

    fun createInstagramIntent(context: Context, fileVideo: File) {
        startActivity(
            context, createShareIntent(context, fileVideo, INSTAGRAM_PACKAGE)
        )
    }

    fun createFacebookIntent(context: Context, fileVideo: File) {
        startActivity(
            context, createShareIntent(context, fileVideo, FACEBOOK_PACKAGE)
        )
    }

    fun createYoutubeIntent(context: Context, fileVideo: File) {
        startActivity(
            context, createShareIntent(context, fileVideo, YOUTUBE_PACKAGE)
        )
    }

    fun createTwitterIntent(context: Context, fileVideo: File) {
        startActivity(
            context, createShareIntent(context, fileVideo, TWITTER_PACKAGE)
        )
    }

    fun createShareMoreIntent(context: Context, fileVideo: File) {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_STREAM,
                createContentUri(
                    context,
                    fileVideo
                )
            )
            type = "video/*"
        }
        context.startActivity(Intent.createChooser(shareIntent, SHARE_VIA))
    }

    fun shareApp(context: Context){
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
            var shareMessage = "\nLet me recommend you this application\n\n"
            shareMessage =
                """${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}""".trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            context.startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: java.lang.Exception) {
            //e.toString();
        }
    }

    private fun createContentUri(context: Context, fileVideo: File): Uri? {
        return FileProvider.getUriForFile(
            context,
            context.packageName + FILE_PROVIDER,
            fileVideo
        )
    }

    private fun createShareIntent(
        context: Context,
        fileSharing: File,
        packageName: String
    ): Intent {
        return Intent().apply {
            setPackage(packageName)
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_STREAM,
                createContentUri(
                    context,
                    fileSharing
                )
            )
            type = "video/*"
        }
    }

    private fun startActivity(context: Context, intent: Intent) {
        try {
            context.startActivity(
                Intent.createChooser(
                    intent,
                    SHARE_VIA
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}