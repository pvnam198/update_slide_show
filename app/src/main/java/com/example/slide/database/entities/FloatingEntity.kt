package com.example.slide.database.entities

import android.graphics.Matrix
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class FloatingEntity() : Parcelable {
    var matrixValues: FloatArray = floatArrayOf()
    val unRotatedWrapperCorner = floatArrayOf()
    val unRotatedPoint = floatArrayOf()
    val boundPoints = floatArrayOf()
    val mappedBounds = floatArrayOf()
    val trappedRect = floatArrayOf()
    val matrix = Matrix()
    val isFlippedHorizontally: Boolean = false
    val isFlippedVertically: Boolean = false
    val isShow = true
}