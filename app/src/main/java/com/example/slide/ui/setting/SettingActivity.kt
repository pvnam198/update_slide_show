package com.example.slide.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.slide.R
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.example.slide.framework.thirdparty.ThirdPartyRequest
import com.example.slide.local.PreferencesHelper
import com.example.slide.ui.about.AboutActivity
import com.example.slide.ui.more.MoreAppAdapter
import com.example.slide.ui.privacy.PrivacyPolicyActivity
import com.example.slide.ui.privacy.TermOfUseActivity
import com.example.slide.ui.vip.VipActivity
import com.example.slide.util.rateApp
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity(), View.OnClickListener {

    private lateinit var moreAppAdapter: MoreAppAdapter

    override fun initViewTools() = InitViewTools({
        R.layout.activity_setting
    })

    companion object {
        fun getInstance(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }

    private var isVip = false

    private var mpackage = "com.amazingmusiceffects.musicrecorder.voicechanger"

    override fun onStart() {
        isVip = PreferencesHelper(this).isVip()
        initVipState()
        super.onStart()
    }

    private fun initVipState() {
        if (isVip) {
            layout_vip.visibility = View.GONE
        } else {
            layout_vip.visibility = View.VISIBLE
        }
    }

    override fun initConfiguration(savedInstanceState: Bundle?) {
        super.initConfiguration(savedInstanceState)
        moreAppAdapter = MoreAppAdapter {
            rateApp(it.vPackage)
        }
        recycler_more_app.adapter = moreAppAdapter

        if (ThirdPartyRequest.apps.isEmpty()) {
            layout_more_app.visibility = View.GONE
        }
    }

    override fun initListener() {
        super.initListener()

        btn_back.setOnClickListener(this)
        //btn_quality.setOnClickListener(this)
        btn_become_vip.setOnClickListener(this)
        btn_rate_us.setOnClickListener(this)
        btn_about.setOnClickListener(this)
        btn_term_of_use.setOnClickListener(this)
        btn_privacy_policy.setOnClickListener(this)
        btn_share_app.setOnClickListener(this)
    }

    override fun releaseData() {

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_back -> {
                onBackPressed()
            }
            /*R.id.btn_quality -> {
                SettingDialogFragment.getInstance(SettingDialogFragment.REQUEST_SETTING)
                    .show(supportFragmentManager, SettingDialogFragment.TAG)
            }*/
            R.id.btn_become_vip -> {
                startActivity(VipActivity.getIntent(this))
            }
            R.id.btn_rate_us -> {
                rateApp(packageName)
            }
            R.id.btn_about -> {
                startActivity(AboutActivity.getInstance(this))
            }
            R.id.btn_term_of_use -> {
                startActivity(TermOfUseActivity.getInstance(this))
            }
            R.id.btn_privacy_policy -> {
                startActivity(PrivacyPolicyActivity.getInstance(this))

            }
            R.id.btn_share_app -> {

            }
        }
    }

}