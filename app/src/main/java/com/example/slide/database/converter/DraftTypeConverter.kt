package com.example.slide.database.converter

import androidx.room.TypeConverter
import com.example.slide.database.entities.FloatingAddedEntity
import com.example.slide.database.entities.FloatingStickerEntity
import com.example.slide.database.entities.FloatingTextEntity
import com.example.slide.database.entities.SavedBy
import com.example.slide.framework.texttovideo.VideoTextExport
import com.example.slide.model.Image
import com.example.slide.music_engine.CropMusic
import com.example.slide.ui.video.video_preview.model.VideoFrame
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray

class DraftTypeConverter {

    @TypeConverter
    fun toSavedBy(value: Int) = enumValues<SavedBy>()[value]

    @TypeConverter
    fun fromSavedBy(value: SavedBy) = value.ordinal

    @TypeConverter
    fun fromImage(value: Image?): String {
        val gson = Gson()
        val type = object : TypeToken<Image>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toImage(value: String): Image? {
        val gson = Gson()
        val type = object : TypeToken<Image>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromImageList(value: ArrayList<Image>): String {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<Image>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toImageList(value: String): ArrayList<Image> {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<Image>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromVideoTextExport(value: VideoTextExport?): String {
        val gson = Gson()
        val type = object : TypeToken<VideoTextExport>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toVideoTextExport(value: String): VideoTextExport? {
        val gson = Gson()
        val type = object : TypeToken<VideoTextExport>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromVideoTextExportList(value: ArrayList<VideoTextExport>): String {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<VideoTextExport>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toVideoTextExportList(value: String): ArrayList<VideoTextExport> {
        return try {
            val gson = Gson()
            val type = object : TypeToken<ArrayList<VideoTextExport>>() {}.type
            gson.fromJson(value, type)
        }catch (e: Exception){
            ArrayList()
        }
    }

    @TypeConverter
    fun fromVideoFrame(value: VideoFrame?): String {
        val gson = Gson()
        val type = object : TypeToken<VideoFrame>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toVideoFrame(value: String): VideoFrame? {
        val gson = Gson()
        val type = object : TypeToken<VideoFrame>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromFloatingAddedEntityList(value: ArrayList<FloatingAddedEntity>): String {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<FloatingAddedEntity>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toFloatingAddedEntityList(value: String): ArrayList<FloatingAddedEntity> {
        val floatingAddedItems = ArrayList<FloatingAddedEntity>()
        val floatingItemArrayJson = JSONArray(value)
        for (i in 0 until floatingItemArrayJson.length()) {
            val gson = Gson()
            when {
                floatingItemArrayJson.getJSONObject(i).has("iconPath") -> {
                    val type = object : TypeToken<FloatingStickerEntity>() {}.type
                    val floatingSticker = gson.fromJson<FloatingStickerEntity>(floatingItemArrayJson.getJSONObject(i).toString(), type)
                    floatingAddedItems.add(floatingSticker)
                }
                floatingItemArrayJson.getJSONObject(i).has("text") -> {
                    val type = object : TypeToken<FloatingTextEntity>() {}.type
                    val floatingText = gson.fromJson<FloatingTextEntity>(floatingItemArrayJson.getJSONObject(i).toString(), type)
                    floatingAddedItems.add(floatingText)
                }
                else -> {
                    val type = object : TypeToken<FloatingAddedEntity>() {}.type
                    val floatingEntity = gson.fromJson<FloatingAddedEntity>(floatingItemArrayJson.getJSONObject(i).toString(), type)
                    floatingAddedItems.add(floatingEntity)
                }
            }
        }
        return floatingAddedItems
    }

    @TypeConverter
    fun fromCropMusicList(value: ArrayList<CropMusic>): String {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<CropMusic>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toCropMusicList(value: String): ArrayList<CropMusic> {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<CropMusic>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromFloatingTextEntity(value: FloatingTextEntity?): String {
        val gson = Gson()
        val type = object : TypeToken<FloatingTextEntity>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toFloatingTextEntity(value: String): FloatingTextEntity? {
        val gson = Gson()
        val type = object : TypeToken<FloatingTextEntity>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromFloatingStickerEntity(value: FloatingStickerEntity?): String {
        val gson = Gson()
        val type = object : TypeToken<FloatingStickerEntity>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toFloatingStickerEntity(value: String): FloatingStickerEntity? {
        val gson = Gson()
        val type = object : TypeToken<FloatingStickerEntity>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromFloatArray(values: FloatArray): String {
        val gson = Gson()
        val type = object : TypeToken<FloatArray>() {}.type
        return gson.toJson(values, type)
    }

    @TypeConverter
    fun toFloatArray(value: String): FloatArray {
        val gson = Gson()
        val type = object : TypeToken<FloatArray>() {}.type
        return gson.fromJson(value, type)
    }
}