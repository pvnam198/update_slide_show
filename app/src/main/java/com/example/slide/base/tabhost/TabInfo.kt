package com.example.slide.base.tabhost

import android.view.View
import androidx.fragment.app.Fragment

class TabInfo(val frame: View, val selectView: View, val tag: String, val createFragment: () -> Fragment) {
    var fragment: Fragment? = null
}