package com.example.slide.base.tabhost

import android.view.View
import androidx.fragment.app.FragmentManager

class TabController(
    private val fragmentManager: FragmentManager,
    private val tabs: Array<TabInfo>
) {

    private var currentTab = -1

    fun openTab(pos: Int) {
        if (currentTab == pos) return
        currentTab = pos
        val tabInfo = tabs[pos]
        if (tabInfo.fragment == null) {
            tabInfo.fragment = tabInfo.createFragment.invoke()
            fragmentManager.beginTransaction()
                .replace(tabInfo.frame.id, tabInfo.fragment!!, tabInfo.tag).commit()
        }
        tabs.forEachIndexed { _, tab ->
            tab.selectView.isSelected = false
            tab.frame.visibility = View.INVISIBLE
        }
        tabInfo.selectView.isSelected = true
        tabInfo.frame.visibility = View.VISIBLE
    }

}