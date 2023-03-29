package com.example.slide.ui.privacy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.slide.R
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : BaseActivity<ActivityPrivacyPolicyBinding>() {
    override fun bindingView(): ActivityPrivacyPolicyBinding {
        return ActivityPrivacyPolicyBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({
        R.layout.activity_privacy_policy
    })

    companion object {
        fun getInstance(context: Context): Intent {
            return Intent(context, PrivacyPolicyActivity::class.java)
        }
    }

    override fun initConfiguration(savedInstanceState: Bundle?) {
        super.initConfiguration(savedInstanceState)
        binding.wvPolicy.loadUrl("file:///android_asset/privacy_policy.html");
    }

    override fun initListener() {
        super.initListener()
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun releaseData() {}
}