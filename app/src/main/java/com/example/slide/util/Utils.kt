package com.example.slide.util

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.provider.Settings
import android.widget.TextView
import com.example.slide.database.dao.DraftDAO
import com.example.slide.ui.select_music.model.MyFile
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.collections.ArrayList

object Utils {

    fun getListFileFromPath(context: Context, path: String): List<MyFile> {

        val musicExtensions = ArrayList<String>()
        musicExtensions.add("mp3")
        musicExtensions.add("wav")
        musicExtensions.add("wma")
        musicExtensions.add("aac")

        val scannedFolders = LocalMusicProvider.getInstance().folderUrls

        val allFiles = ArrayList<MyFile>()
        val folders = ArrayList<MyFile>()
        val files = ArrayList<MyFile>()
        val root = File(path)
        val dirs = root.listFiles()
        if (dirs != null) {
            Arrays.sort(dirs) { lhs, rhs -> lhs.name.compareTo(rhs.name) }

            for (i in dirs.indices) {
                if (!dirs[i].name.startsWith(".")) {
                    if (dirs[i].isDirectory) {
                        val file = MyFile(dirs[i].absolutePath, dirs[i].name, true)
                        if (scannedFolders.contains(dirs[i].absolutePath.substring(1)))
                            file.setSongFolder(true)
                        folders.add(file)
                    } else {
                        if (dirs[i].name.lastIndexOf(".") == -1)
                            continue
                        if (musicExtensions.contains(
                                dirs[i].name.substring(
                                    dirs[i].name.lastIndexOf(
                                        "."
                                    ) + 1
                                )
                            )
                        )
                            files.add(MyFile(dirs[i].absolutePath, dirs[i].name, false))
                    }
                }
            }

            allFiles.addAll(folders)
            allFiles.addAll(files)
        }
        return allFiles
    }

    fun getListFileHeaderFromPath(path: String): List<File> {
        val files = ArrayList<File>()
        var root = File(path)
        files.add(root)
        while (root.absolutePath != MyStatic.EXTERNAL_STORAGE_PATH && root.absolutePath.contains("/")) {
            root = File(root.absolutePath.substring(0, root.absolutePath.lastIndexOf("/")))
            files.add(0, root)
        }
        return files
    }

    fun isDevMode(context: Context): Boolean {
        return Settings.Secure.getInt(
            context.applicationContext.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
        ) != 0
    }

    fun isScopeStorage(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    fun getUniqueVideoFileName(): String {
//        if (isScopeStorage()) return originFileName
        val originFileName = SimpleDateFormat("MMM dd, hh.mm a", Locale.US).format(Date())
        var lFileName = originFileName
        if (File(MyStatic.FOLDER_SHARK_VIDEO + lFileName + ".mp4").exists()) {
            var index = 1
            while (File(MyStatic.FOLDER_SHARK_VIDEO + originFileName + " (" + index + ").mp4").exists()) {
                index++
                if (index == 1000) break
            }
            lFileName = "$originFileName ($index)"
            return lFileName
        }
        return lFileName
    }

    fun setTypeface(context: Context, textView: TextView, type: String = "") {
        val typeface =
            if (type.isEmpty()) FontProvider.getDefaultFont()
            else Typeface.createFromAsset(context.assets, type)
        textView.typeface = typeface
    }

    fun convertingMillisecondsToHours(millis: Long): String {
        return String.format(
            Locale.US,
            "%d min, %d sec",
            TimeUnit.MILLISECONDS.toMinutes(millis),
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        )
    }
}