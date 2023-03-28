package com.example.slide.ui.my_studio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.slide.R
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_my_studio.*

class MyStudioActivity : BaseActivity() {

    companion object {

        const val REQUEST_DELETE_FILE = 11

        private const val TAG = "MyStudioActivity"

        const val VIDEO_MODE = 0

        const val DRAFT_MODE = 1

        const val EXTRA_MODE = "MODE"

        fun getCallingIntent(context: Context, mode: Int): Intent {
            return Intent(context, MyStudioActivity::class.java).apply {
                putExtra(EXTRA_MODE, mode)
            }
        }

    }

    override fun initViewTools() = InitViewTools({ R.layout.activity_my_studio })

    override fun releaseData() {
    }

    private inner class StudioViewPagerAdapter(
            fragmentManager: FragmentManager,
            lifecycle: Lifecycle
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> VideoFragment()
                1 -> DraftFragment()
                else -> VideoFragment()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        btn_back.setOnClickListener { finish() }
        viewPager.adapter = StudioViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.offscreenPageLimit = 2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.txt_video)
                1 -> tab.text = getString(R.string.txt_drafts)
            }
            viewPager.setCurrentItem(tab.position, true)
        }.attach()
        with(intent.getIntExtra(EXTRA_MODE, 0)) {
            viewPager.setCurrentItem(this, false)
        }
        setUpTabLayout()
    }

    private fun setUpTabLayout() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        for (i in 0 until tabLayout.tabCount) {
            val tab: View = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            (tab.layoutParams as ViewGroup.MarginLayoutParams).setMargins(
                    displayMetrics.widthPixels / 8,
                    0,
                    displayMetrics.widthPixels / 8,
                    0
            )
            tab.requestLayout()
        }
    }
}