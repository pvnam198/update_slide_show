package com.example.slide.ui.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.example.slide.BuildConfig
import com.example.slide.R
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.example.slide.ui.privacy.PrivacyPolicyActivity
import com.example.slide.ui.privacy.TermOfUseActivity
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_about.btn_privacy_policy
import kotlinx.android.synthetic.main.activity_about.btn_term_of_use
import java.lang.Exception

class AboutActivity : BaseActivity(), View.OnClickListener {

    override fun initViewTools() = InitViewTools({
        R.layout.activity_about
    })

    companion object {
        fun getInstance(context: Context): Intent {
            return Intent(context, AboutActivity::class.java)
        }
    }

    override fun initConfiguration(savedInstanceState: Bundle?) {
        super.initConfiguration(savedInstanceState)
        tv_version.text = getString(R.string.app_version_format, BuildConfig.VERSION_NAME)
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener(this)
        btn_privacy_policy.setOnClickListener(this)
        btn_term_of_use.setOnClickListener(this)
        btn_feedback.setOnClickListener(this)
    }

    override fun releaseData() {}

    override fun onClick(view: View) {
        when (view) {
            btn_back -> {
                onBackPressed()
            }
            btn_feedback -> {
                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.type = "text/plain"
                val aEmailList =
                    arrayOf(tv_email.text.toString())
                emailIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList)
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
                intent.data = Uri.parse("mailto:sharkstudio.imusic@gmail.com")
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."))
//                    startActivity(emailIntent)
                } catch (e: Exception) {

                }
            }
            btn_term_of_use -> {
                startActivity(TermOfUseActivity.getInstance(this))
            }
            btn_privacy_policy -> {
                startActivity(PrivacyPolicyActivity.getInstance(this))
            }
        }
    }
}