package com.example.slide.model

class Album(
    val id: String,
    var name: String = "",
    var imageUrl: String = "",
    var imageList: ArrayList<Image> = arrayListOf()
)