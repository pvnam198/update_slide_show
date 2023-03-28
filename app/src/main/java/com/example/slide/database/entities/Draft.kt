package com.example.slide.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.slide.framework.texttovideo.VideoTextExport
import com.example.slide.model.Image
import com.example.slide.music_engine.CropMusic
import com.example.slide.ui.video.video_preview.model.VideoFrame
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "draft")
@Parcelize
data class Draft(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,

        var title: String = "",

        var totalDuration: Int = 0,

        var images: ArrayList<Image> = ArrayList(),

        var totalImage: Int = 0,

        var texts: ArrayList<VideoTextExport> = ArrayList(),

        var videoFrame: VideoFrame? = null,

        var themeId: Int = 6,

        var floatingItemsAdded: ArrayList<FloatingAddedEntity> = ArrayList(),

        var cropMusic: ArrayList<CropMusic> = ArrayList(),

        var createdAt: Long = 0,

        var modifiedAt: Long = 0
) : Parcelable