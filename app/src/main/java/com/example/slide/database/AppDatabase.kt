package com.example.slide.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.slide.database.converter.DraftTypeConverter
import com.example.slide.database.dao.DraftDAO
import com.example.slide.database.entities.Draft

@Database(entities = [Draft::class], version = 1)
@TypeConverters(DraftTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun draftDAO(): DraftDAO

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(
            context: Context
        ): AppDatabase = instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "slide-database"
            ).allowMainThreadQueries().build()
        }
    }
}
