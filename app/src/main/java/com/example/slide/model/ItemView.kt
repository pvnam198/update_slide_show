package com.example.slide.model

import android.graphics.Bitmap
import android.view.View

class ItemView(v: View, bm: Bitmap) {
    var view: View? = v
    var bm: Bitmap? = bm
    var text: String? = null
    var textColor = 0
}