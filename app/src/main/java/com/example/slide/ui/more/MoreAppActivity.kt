package com.example.slide.ui.more

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.slide.R
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.ActivityMoreAppBinding
import com.example.slide.util.rateApp

class MoreAppActivity : BaseActivity<ActivityMoreAppBinding>() {

    private lateinit var moreAppAdapter: MoreAppAdapter
    override fun bindingView(): ActivityMoreAppBinding {
        return ActivityMoreAppBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({
        R.layout.activity_more_app
    })

    override fun initConfiguration(savedInstanceState: Bundle?) {
        super.initConfiguration(savedInstanceState)
        moreAppAdapter = MoreAppAdapter {
            rateApp(it.vPackage)
        }

        binding.recyclerApp.adapter = moreAppAdapter
    }

    override fun initListener() {
        super.initListener()
        binding.btnBack.setOnClickListener { onBackPressed() }
    }

    override fun releaseData() {
    }

    companion object {
        fun getCallingIntent(context: Context): Intent {
            return Intent(context, MoreAppActivity::class.java)
        }
    }
}