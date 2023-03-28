package com.example.slide.ui.more

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.slide.R
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.example.slide.util.rateApp
import kotlinx.android.synthetic.main.activity_more_app.*

class MoreAppActivity : BaseActivity() {

    private lateinit var moreAppAdapter: MoreAppAdapter

    override fun initViewTools() = InitViewTools({
        R.layout.activity_more_app
    })

    override fun initConfiguration(savedInstanceState: Bundle?) {
        super.initConfiguration(savedInstanceState)
        moreAppAdapter = MoreAppAdapter {
            rateApp(it.vPackage)
        }

        recycler_app.adapter = moreAppAdapter
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener { onBackPressed() }
    }

    override fun releaseData() {
    }

    companion object {
        fun getCallingIntent(context: Context): Intent {
            return Intent(context, MoreAppActivity::class.java)
        }
    }
}