package com.example.slide.repository

import android.content.Context
import android.util.Log
import com.example.slide.MyApplication
import com.example.slide.database.AppDatabase
import com.example.slide.database.entities.Draft
import com.example.slide.util.FileUtils
import com.example.slide.util.copyFolder
import kotlinx.coroutines.*

class DraftRepository(val context: Context) {

    private val draftDAO = AppDatabase.getInstance(context).draftDAO()

    suspend fun getListDraft(numberItem: Int) = withContext(Dispatchers.IO) {
        draftDAO.getListDraft(numberItem)
    }

    suspend fun getAllDraft() = withContext(Dispatchers.IO) {
        draftDAO.getAllDraft()
    }

    suspend fun deleteInvalidDraft() = withContext(Dispatchers.IO) {
        draftDAO.deleteInvalidDraft()
    }

    suspend fun getDraft(draftId: Long) = withContext(Dispatchers.IO) {
        draftDAO.getDraft(draftId)
    }

    fun getDraftMain(draftId: Long) = draftDAO.getDraftMain(draftId)

    suspend fun get(draftId: Long) = withContext(Dispatchers.IO) {
        draftDAO.getDraft(draftId)
    }

    fun getDraftCount(): Int = runBlocking {
        val count = async {
            draftDAO.getCount()
        }
        count.start()
        count.await()
    }

    fun saveAsDraft(draft: Draft): Long {
        draft.apply {
            totalImage = this.images.size
            createdAt = System.currentTimeMillis()
            modifiedAt = System.currentTimeMillis()
        }
        return draftDAO.insertWithTimestamp(draft)
    }

    suspend fun deleteDraft(draftId: Long) {
        draftDAO.deleteDraft(draftId)
        FileUtils.removeTempFolderFromDraft(context,draftId)
    }

    suspend fun renameDraft(newName: String, draftId: Long) = withContext(Dispatchers.IO) {
        val oldDraft = getDraft(draftId = draftId)[0]
        draftDAO.updateWithTimestamp(oldDraft.copy(title = newName))
    }

    suspend fun duplicateDraft(draftId: Long) = withContext(Dispatchers.IO) {
        val oldDraft = getDraft(draftId = draftId)[0]

        var index = 0
        var uniqueTitle = oldDraft.title

        while (draftDAO.isExistWithTitle(uniqueTitle)) {
            index++
            if (index == 1000) break
            uniqueTitle = "${oldDraft.title} ($index)"
        }

        val insertedId = draftDAO.insertWithTimestamp(
            Draft(
                title = uniqueTitle,
                totalDuration = MyApplication.getInstance().videoDataState.calculateDurationWithImages(
                    oldDraft.images.size
                ),
                totalImage = oldDraft.images.size,
                images = oldDraft.images,
                texts = oldDraft.texts,
                videoFrame = oldDraft.videoFrame,
                themeId = oldDraft.themeId,
                floatingItemsAdded = oldDraft.floatingItemsAdded,
                cropMusic = oldDraft.cropMusic,
                createdAt = System.currentTimeMillis(),
                modifiedAt = System.currentTimeMillis()
            )
        )
        if (insertedId >= 0) {
            val newDraft = getDraft(draftId = insertedId)[0]

            FileUtils.getImagesTempDir(context, draftId)
                .copyFolder(FileUtils.getImagesTempDir(context, insertedId))
            FileUtils.getMusicDir(context, draftId)
                .copyFolder(FileUtils.getMusicDir(context, insertedId))
            var needUpdate = false
            newDraft.images.forEachIndexed { _, image ->
                if (image.url.startsWith(context.filesDir.absolutePath)) {
                    needUpdate = true
                    val url = image.url
                    val posfix = url.substring(url.lastIndexOf("/"))
                    var prefix = url.substring(0, url.lastIndexOf("/"))
                    prefix = prefix.substring(0, prefix.lastIndexOf("/") + 1)
                    image.url = prefix + insertedId + posfix
                }
            }
            newDraft.cropMusic.forEachIndexed { _, cropMusic ->
                if (cropMusic.track!=null && cropMusic.track.url.startsWith(context.filesDir.absolutePath)) {
                    needUpdate = true
                    val url = cropMusic.track.url
                    val posfix = url.substring(url.lastIndexOf("/"))
                    var prefix = url.substring(0, url.lastIndexOf("/"))
                    prefix = prefix.substring(0, prefix.lastIndexOf("/") + 1)
                    cropMusic.track.url = prefix + insertedId + posfix
                }
            }
            if (needUpdate) {
                draftDAO.insertWithTimestamp(newDraft)
            }
        }


    }
}