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
import com.example.slide.databinding.ActivityAboutBinding
import com.example.slide.ui.privacy.PrivacyPolicyActivity
import com.example.slide.ui.privacy.TermOfUseActivity
import java.lang.Exception

class AboutActivity : BaseActivity<ActivityAboutBinding>(), View.OnClickListener {
    override fun bindingView(): ActivityAboutBinding {
        return ActivityAboutBinding.inflate(layoutInflater)
    }

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
        binding.tvVersion.text = getString(R.string.app_version_format, BuildConfig.VERSION_NAME)
    }

    override fun initListener() {
        super.initListener()
        binding.btnBack.setOnClickListener(this)
        binding.btnPrivacyPolicy.setOnClickListener(this)
        binding.btnTermOfUse.setOnClickListener(this)
        binding.btnFeedback.setOnClickListener(this)
    }

    override fun releaseData() {}

    override fun onClick(view: View) {
        when (view) {
            binding.btnBack -> {
                onBackPressed()
            }
            binding.btnFeedback -> {
                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.type = "text/plain"
                val aEmailList =
                    arrayOf(binding.tvEmail.text.toString())
                emailIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList)
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
                intent.data = Uri.parse("mailto:sharkstudio.imusic@gmail.com")
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."))
//                    startActivity(emailIntent)
                } catch (e: Exception) {

                }
            }
            binding.btnTermOfUse -> {
                startActivity(TermOfUseActivity.getInstance(this))
            }
            binding.btnPrivacyPolicy -> {
                startActivity(PrivacyPolicyActivity.getInstance(this))
            }
        }
    }
}