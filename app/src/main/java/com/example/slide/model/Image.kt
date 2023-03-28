package com.example.slide.model

import java.io.Serializable

data class Image(
    var id: Long,
    val albumId: String,
    val albumName: String?,
    var url: String,
    val name: String?,
    var countNumber: Int = 0,
    var uniqueId: Float = -1f
) : Serializable {

    fun resetCount() {
        countNumber = 0
    }

    fun increaseSelectCount() {
        countNumber++
    }

    fun isSelected(): Boolean = countNumber > 0
}