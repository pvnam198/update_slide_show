package com.example.slide.ui.privacy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.slide.R
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.ActivityTermsAndConditionsBinding

class TermOfUseActivity : BaseActivity<ActivityTermsAndConditionsBinding>() {
    override fun bindingView(): ActivityTermsAndConditionsBinding {
        return ActivityTermsAndConditionsBinding.inflate(layoutInflater)
    }

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
        binding.wvTerm.loadUrl("file:///android_asset/terms_conditions.html");

    }

    override fun initListener() {
        super.initListener()
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun releaseData() {}
}