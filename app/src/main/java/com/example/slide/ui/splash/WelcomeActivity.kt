package com.example.slide.ui.splash

import android.content.Context
import android.content.Intent
import android.view.View
import com.example.slide.R
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.example.slide.local.PreferencesHelper
import com.example.slide.ui.home.MainActivity
import com.example.slide.ui.privacy.PrivacyPolicyActivity
import com.example.slide.ui.privacy.TermOfUseActivity
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : BaseActivity(), View.OnClickListener {

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

        btn_start.setOnClickListener(this)
        btn_term_of_use.setOnClickListener(this)
        btn_privacy_policy.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view) {
            btn_start -> {
                preferencesHelper.setOpenFistTime()
                preferencesHelper.saveTermAndConditionMode(true)
                finish()
                startActivity(MainActivity.getCallingIntent(this))
            }
            btn_term_of_use -> {
                startActivity(TermOfUseActivity.getInstance(this))
            }
            btn_privacy_policy -> {
                startActivity(PrivacyPolicyActivity.getInstance(this))
            }
        }
    }

    override fun releaseData() {}
}