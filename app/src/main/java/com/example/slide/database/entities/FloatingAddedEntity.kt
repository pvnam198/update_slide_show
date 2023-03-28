package com.example.slide.database.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class FloatingAddedEntity : FloatingEntity(), Parcelable {
    var startTime: Int = 0
    var endTime: Int = 0
    var isFullTime = true
}