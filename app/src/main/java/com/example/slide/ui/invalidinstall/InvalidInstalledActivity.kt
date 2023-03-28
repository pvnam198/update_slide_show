package com.example.slide.ui.invalidinstall

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.example.slide.MyApplication
import com.example.slide.R
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_invalid_installed.*

class InvalidInstalledActivity : BaseActivity() {

    override fun initViewTools() = InitViewTools({ R.layout.activity_invalid_installed })

    override fun releaseData() {
    }

    override fun initConfiguration(savedInstanceState: Bundle?) {
        super.initConfiguration(savedInstanceState)
        btn_back.setOnClickListener { onBackPressed() }
        btn_go_to_store.setOnClickListener {
            goToApp(packageName)
        }
    }

    override fun onStart() {
        super.onStart()
        MyApplication.getInstance().setIsFullScreenAds(true)
    }

    override fun onStop() {
        super.onStop()
        MyApplication.getInstance().setIsFullScreenAds(false)
    }

    private fun goToApp(packageString: String) {
        FirebaseAnalytics.getInstance(this).logEvent("go_to_store_from_unknown_source", null)
        val uri = Uri.parse("market://details?id=$packageString")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageString")
                )
            )
        }
    }

}
