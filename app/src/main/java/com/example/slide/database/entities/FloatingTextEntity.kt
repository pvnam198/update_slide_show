package com.example.slide.database.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FloatingTextEntity(
        val textWidth: Int,
        val textHeight: Int,
        val textAlpha: Int,
        val textColor: Int,
        val fontName: String,
        val textSize: Int,
        val paddingWidth: Int,
        val paddingHeight: Int,
        val backgroundColor: Int,
        val backgroundAlpha: Int,
        val backgroundBorder: Int,
        val isShowBackground: Boolean,
        val id: Long,
        val text: String?,
        val isHandling: Boolean
) : FloatingAddedEntity(), Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FloatingTextEntity

        if (textWidth != other.textWidth) return false
        if (textHeight != other.textHeight) return false
        if (textAlpha != other.textAlpha) return false
        if (textColor != other.textColor) return false
        if (paddingWidth != other.paddingWidth) return false
        if (paddingHeight != other.paddingHeight) return false
        if (backgroundColor != other.backgroundColor) return false
        if (backgroundAlpha != other.backgroundAlpha) return false
        if (backgroundBorder != other.backgroundBorder) return false
        if (isShowBackground != other.isShowBackground) return false
        if (id != other.id) return false
        if (text != other.text) return false
        if (isHandling != other.isHandling) return false
        if (!matrixValues.contentEquals(other.matrixValues)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = textWidth
        result = 31 * result + textHeight
        result = 31 * result + textAlpha
        result = 31 * result + textColor
        result = 31 * result + paddingWidth
        result = 31 * result + paddingHeight
        result = 31 * result + backgroundColor
        result = 31 * result + backgroundAlpha
        result = 31 * result + backgroundBorder
        result = 31 * result + isShowBackground.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + (text?.hashCode() ?: 0)
        result = 31 * result + isHandling.hashCode()
        result = 31 * result + matrixValues.contentHashCode()
        return result
    }
}