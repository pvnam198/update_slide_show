package com.example.slide.ads

import android.app.Activity
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.*

object BannerHelper {

    fun loadAdaptiveBanner(activity: Activity,adView: AdView, parentView: View,childView: ViewGroup, loadingView: View) {
        childView.addView(adView)
        parentView.visibility = View.VISIBLE
        loadingView.visibility = View.VISIBLE
        adView.adUnitId = Ads.getBannerAdsId()
        adView.adSize = getAdsSize(activity,childView)
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                parentView.visibility = View.VISIBLE
                loadingView.visibility = View.GONE
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                parentView.visibility = View.GONE
            }
        }
        val adRequest = AdRequest
            .Builder()
            .build()
        adView.loadAd(adRequest)
    }

    private fun getAdsSize(activity: Activity, view: View) : AdSize {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val density = outMetrics.density
        var adWidthPixels = view.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }
}