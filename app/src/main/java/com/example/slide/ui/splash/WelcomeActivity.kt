package com.example.slide.ui.splash

import android.content.Context
import android.content.Intent
import android.view.View
import com.example.slide.R
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.ActivityWelcomeBinding
import com.example.slide.local.PreferencesHelper
import com.example.slide.ui.home.MainActivity
import com.example.slide.ui.privacy.PrivacyPolicyActivity
import com.example.slide.ui.privacy.TermOfUseActivity

class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>(), View.OnClickListener {
    override fun bindingView(): ActivityWelcomeBinding {
        return ActivityWelcomeBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({
        R.layout.activity_welcome
    })

    companion object {

        fun getInstance(context: Context): Intent {
            return Intent(context, WelcomeActivity::class.java)
        }
    }

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(this)
    }

    override fun initListener() {
        super.initListener()

        binding.btnStart.setOnClickListener(this)
        binding.btnTermOfUse.setOnClickListener(this)
        binding.btnPrivacyPolicy.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnStart -> {
                preferencesHelper.setOpenFistTime()
                preferencesHelper.saveTermAndConditionMode(true)
                finish()
                startActivity(MainActivity.getCallingIntent(this))
            }
            binding.btnTermOfUse -> {
                startActivity(TermOfUseActivity.getInstance(this))
            }
            binding.btnPrivacyPolicy -> {
                startActivity(PrivacyPolicyActivity.getInstance(this))
            }
        }
    }

    override fun releaseData() {}
}