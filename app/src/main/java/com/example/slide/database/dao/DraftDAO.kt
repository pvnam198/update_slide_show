package com.example.slide.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.slide.database.entities.Draft

@Dao
interface DraftDAO : BaseDAO<Draft> {

    @Query("SELECT * FROM draft WHERE totalImage > 1 ORDER BY modifiedAt DESC")
    suspend fun getAllDraft(): List<Draft>

    @Query("DELETE FROM draft WHERE totalImage < 2")
    suspend fun deleteInvalidDraft()

    @Query("SELECT * FROM draft WHERE id = :draftId")
    suspend fun getDraft(draftId: Long): List<Draft>

    @Query("SELECT * FROM draft WHERE id = :draftId")
    fun getDraftMain(draftId: Long): List<Draft>

    @Query("SELECT COUNT(*) FROM draft WHERE totalImage > 1")
    suspend fun getCount(): Int

    @Query("DELETE FROM draft WHERE id = :draftId")
    suspend fun deleteDraft(draftId: Long)

    @Query("SELECT * FROM draft WHERE totalImage > 1 ORDER BY modifiedAt DESC LIMIT :numberItem")
    suspend fun getListDraft(numberItem: Int): List<Draft>

    @Query("SELECT EXISTS(SELECT * FROM draft WHERE title = :title)")
    suspend fun isExistWithTitle(title: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWithTimestamp(draft: Draft): Long

    suspend fun updateWithTimestamp(draft: Draft) {
        insertOrUpdate(draft.apply {
            modifiedAt = System.currentTimeMillis()
        })
    }
}