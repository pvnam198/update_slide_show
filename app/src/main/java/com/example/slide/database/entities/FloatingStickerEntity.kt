package com.example.slide.database.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FloatingStickerEntity(val iconPath: String) : FloatingAddedEntity(), Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FloatingStickerEntity

        if (iconPath != other.iconPath) return false
        if (!matrixValues.contentEquals(other.matrixValues)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = iconPath.hashCode()
        result = 31 * result + matrixValues.contentHashCode()
        return result
    }
}