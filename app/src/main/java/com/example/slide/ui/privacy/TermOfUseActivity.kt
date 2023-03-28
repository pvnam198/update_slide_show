package com.example.slide.ui.privacy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.slide.R
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import kotlinx.android.synthetic.main.activity_terms_and_conditions.*

class TermOfUseActivity : BaseActivity() {

    override fun initViewTools() = InitViewTools({
        R.layout.activity_terms_and_conditions
    })

    companion object {
        fun getInstance(context: Context): Intent {
            return Intent(context, TermOfUseActivity::class.java)
        }
    }

    override fun initConfiguration(savedInstanceState: Bundle?) {
        super.initConfiguration(savedInstanceState)
        wv_term.loadUrl("file:///android_asset/terms_conditions.html");

    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun releaseData() {}
}