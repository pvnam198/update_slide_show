package com.example.slide.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

open class BaseFragmentStateAdapter(
    private val mListFragment: MutableList<Fragment> = mutableListOf(),
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int = mListFragment.size

    override fun createFragment(position: Int): Fragment {
        return mListFragment[position]
    }
}