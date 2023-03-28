package com.example.slide.util

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import com.bumptech.glide.Glide
import com.example.slide.ui.video.video_preview.model.VideoFrame
import com.example.slide.videolib.VideoConfig.getVideoHeight
import com.example.slide.videolib.VideoConfig.getVideoWidth
import org.apache.commons.io.FileUtils
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.Normalizer
import java.util.*
import java.util.regex.Pattern

object FileUtils {

    private const val IMAGE_DIR = "image_dir"

    private const val FRAME_DIR = "frame_dir"

    private const val MUSIC_DIR = "music_dir"

    private const val PREVIEW_IMAGE_DIR = "preview_image_dir"

    private const val MUSIC_DEFAULT_DIR = "music_default_dir"

    const val FRAME_FILE_NAME = "frame.png"

    const val MUSIC_FILE_NAME = "music.mp3"

    const val VIDEO_FILE_NAME = "video.mp4"

    const val VIDEO_CONFIG_FILE_NAME = "video.txt"

    const val AUDIO_CONFIG_FILE_NAME = "merge_audio.txt"

    const val AUDIO_MERGE_FILE_NAME = "/merge.mp3"

    fun getAudioMergeFile(context: Context): File {
        val file = File(context.filesDir, AUDIO_MERGE_FILE_NAME)
        return file
    }

    fun getAudioMergeConfigFile(context: Context): File {
        val file = File(context.filesDir, AUDIO_CONFIG_FILE_NAME)
        return file
    }

    fun getVideoConfigFile(context: Context): File {
        val file = File(context.filesDir, VIDEO_CONFIG_FILE_NAME)
        /*   if (!file.exists())
               file.mkdirs()*/
        return file
    }

    fun getFramesTempDir(context: Context): File {
        val file = File(context.filesDir, FRAME_DIR)
        file.mkdirs()
        return file
    }

    fun getFrameFile(context: Context): File {
        val dir = getFramesTempDir(context)
        val file = File(dir, FRAME_FILE_NAME)
        return file
    }

    fun getPrivatePreviewImageDirectory(
        context: Context,
        theme: String?,
        iNo: Int
    ): File {
        val imageDir = File(
            getImagesPreviewTempDir(context),
            String.format(Locale.US, "IMG_%03d", iNo)
        )
        if (!imageDir.exists()) {
            imageDir.mkdirs()
        }
        return imageDir
    }

    fun getImagesTempDir(context: Context, draftId: Long): File {
        val tempDir = File(context.filesDir, IMAGE_DIR.plus("/").plus(draftId))
        tempDir.mkdirs()
        return tempDir
    }

    fun getImagesPreviewTempDir(context: Context): File {
        val tempDir = File(context.filesDir, PREVIEW_IMAGE_DIR)
        tempDir.mkdirs()
        return tempDir
    }

    fun getImagesTempDirName(): String {
        return IMAGE_DIR
    }

    fun getMusicDir(context: Context, draftId: Long): File {
        val tempDir = File(context.filesDir, MUSIC_DIR.plus("/").plus(draftId))
        tempDir.mkdirs()
        return tempDir
    }

    fun getMusicDefaultDir(context: Context): File {
        val tempDir = File(context.filesDir, MUSIC_DEFAULT_DIR)
        if (!tempDir.exists())
            tempDir.mkdirs()
        return tempDir
    }

    fun removeFolder(context: Context, nameFolder: String) {
        val dir = File(context.filesDir, nameFolder)
        if (dir.exists() && dir.isDirectory) {
            dir.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    removeFolder(context, file.name)
                } else {
                    file.delete()
                }
            }
        }
        dir.delete()
    }

    fun getMusicTempDirName(): String {
        return MUSIC_DIR
    }

    private fun removeDefaultMusicDir(context: Context) {
        val dir = getMusicDefaultDir(context)
        if (dir.exists())
            dir.delete()
    }

    fun removeTempFolderFromDraft(context: Context, draftId: Long) {
        removeMusicTempDir(context, draftId)
        removeImageTempDir(context, draftId)
    }

    private fun removeMusicTempDir(context: Context, draftId: Long) {
        val dir = getMusicDir(context, draftId)
        FileUtils.deleteDirectory(dir)
    }

    private fun removeImageTempDir(context: Context, draftId: Long) {
        Timber.d("Removing image temp dir")
        val dir = getImagesTempDir(context, draftId)
        Timber.d("Dir exist: ${dir.exists()}")
        FileUtils.deleteDirectory(dir)
    }

    fun removePreviewImageTempDir(context: Context) {
        Timber.d("Removing preview image temp dir")
        val dir = getImagesPreviewTempDir(context)
        Timber.d("Dir exist: ${dir.exists()}")
        FileUtils.deleteDirectory(dir)
    }

    private fun removeFrameTempDir(context: Context) {
        val dir = getFramesTempDir(context)
        if (dir.exists() && dir.isDirectory) {
            dir.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    removeFolder(context, file.name)
                } else {
                    file.delete()
                }
            }
        }
    }

    private fun getTempDir(context: Context, tempDirName: String): File {
        val tempDir = File(context.filesDir, tempDirName)
        tempDir.mkdirs()
        return tempDir
    }

    fun convertNormalFullFileName(filePath: String): String {
        val prefix = filePath.substring(filePath.lastIndexOf("."))
        val originFileName =
            filePath.substring(filePath.lastIndexOf("/"), filePath.lastIndexOf("."))

        val nfdNormalizedString: String = Normalizer.normalize(originFileName, Normalizer.Form.NFD)
        val pattern: Pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        var resultPattern: String = pattern.matcher(nfdNormalizedString).replaceAll("")
        val re = Regex("[^A-Za-z0-9()-, ]")
        resultPattern = re.replace(resultPattern, " ")
        return resultPattern.trim() + prefix
    }

    private fun convertNormalFileName(originFileName: String): String {
        val nfdNormalizedString: String = Normalizer.normalize(originFileName, Normalizer.Form.NFD)
        val pattern: Pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        var resultPattern: String = pattern.matcher(nfdNormalizedString).replaceAll("")
        val re = Regex("[^A-Za-z0-9()-, ]")
        resultPattern = re.replace(resultPattern, " ")
        return resultPattern.trim()
    }

    fun getUniqueAudioTrimmedFileUrl(context: Context, originalUrl: String, draftId: Long): String {
        val musicDir = getMusicDir(context, draftId).absolutePath.plus("/")
        Timber.d("GetUni: Music Dir - $musicDir")
        val prefix = originalUrl.substring(originalUrl.lastIndexOf("."))
        Timber.d("GetUni: Prefix - $prefix")
        var originFileName =
            originalUrl.substring(originalUrl.lastIndexOf("/"), originalUrl.lastIndexOf("."))

        Timber.d("GetUni: OriginFileNameBefore - $originFileName")

        originFileName = convertNormalFileName(originFileName)

        Timber.d("GetUni: OriginFileNameAfter - $originFileName")

        Timber.d("GetUni: Check - $musicDir$originFileName$prefix")
        if (File(musicDir + originFileName + prefix).exists()) {
            var index = 1
            while (File("$musicDir$originFileName ($index)$prefix").exists()) {
                index++
                if (index == 1000) break
            }
            return "$musicDir$originFileName ($index)$prefix"
        }
        return musicDir + originFileName + prefix
    }

    fun deleteVideoFromDevice(context: Context, id: Long): Boolean {
        val projection = arrayOf(BaseColumns._ID, MediaStore.MediaColumns.DATA)
        val selection = StringBuilder()
        selection.append(BaseColumns._ID + " IN (")
        selection.append(id)
        selection.append(")")

        try {
            val cursor = context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection.toString(),
                null,
                null
            )
            if (cursor != null) {

                // Step 2: Remove selected tracks from the database
                context.contentResolver.delete(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    selection.toString(), null
                )

                // Step 3: Remove files from card
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val name = cursor.getString(1)
                    try { // File.delete can throw a security exception
                        val f = File(name)
                        if (!f.delete()) {
                        }
                        cursor.moveToNext()
                    } catch (ex: SecurityException) {
                        cursor.moveToNext()
                    } catch (e: NullPointerException) {
                        Log.e("MusicUtils", "Failed to find file $name")
                    }

                }
                cursor.close()
            }
            context.contentResolver.notifyChange(Uri.parse("content://media"), null)
            return true
        } catch (ignored: SecurityException) {
            return false
        }
    }

    fun saveToInternalStorage(bitmapImage: Bitmap, context: Context): String? {
        val cw = ContextWrapper(context.applicationContext)
        // path to /data/data/your_app/app_data/imageDir
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        // Create imageDir
        val imageFile = File(directory, "frame.jpg")
        if (imageFile.exists())
            imageFile.delete()
        Log.d("fpi", imageFile.name)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(imageFile)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.absolutePath
    }

    fun saveFrameToInternalStorage(context: Context, frame: VideoFrame) {
        val frameFile = getFrameFile(context)
        if (frameFile.exists()) {
            frameFile.delete()
        }
        val futureTarget = Glide.with(context).asBitmap().load(frame.getUri()).submit()
        val fileOutputStream = FileOutputStream(frameFile)
        val outbitmap =
            Bitmap.createScaledBitmap(futureTarget.get(), getVideoWidth(), getVideoHeight(), false)
        outbitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
    }

    fun deleteFile(path: String) {
        val someDir = File(path)
        someDir.deleteRecursively()
    }

    fun removeAllInternalFile(context: Context) {
        context.filesDir.listFiles()?.let { files ->
            val fileList: Array<File> = files
            fileList.forEach { file ->
                file.delete()
            }
        }
    }

    fun deleteFile(mFile: File?): Boolean {
        var idDelete = false
        var deleteFileCount = 0L
        if (mFile == null) {
            return true
        }
        if (mFile.exists()) {
            if (mFile.isDirectory) {
                val children = mFile.listFiles()
                if (children != null && children.isNotEmpty()) {
                    for (child in children) {
                        deleteFileCount += child.length()
                        idDelete = deleteFile(child)
                    }
                }
                deleteFileCount += mFile.length()
                idDelete = mFile.delete()
            } else {
                deleteFileCount += mFile.length()
                idDelete = mFile.delete()
            }
        }
        return idDelete
    }

}