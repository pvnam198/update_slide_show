package com.example.slide.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDAO<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(entity: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(entities: List<T>)

    @Insert
    fun insert(entity: T): Long

    @Insert
    fun insert(entities: List<T>)

    @Update
    fun update(entity: T)

    @Update
    fun update(entities: List<T>)

    @Delete
    fun delete(entity: T)

    @Delete
    fun delete(entities: List<T>)

}
