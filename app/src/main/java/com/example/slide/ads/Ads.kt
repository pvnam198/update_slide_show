package com.example.slide.ads

import android.content.Context
import com.example.slide.BuildConfig
import com.example.slide.MyApplication
import com.example.slide.local.PreferencesHelper

object Ads {

    var notification_setup_h = 11

    var notification_setup_m = 0

    var notification_content = 1

    var startAppTimeThreshold = 3600000L

    var popupAdsGap = 180000L

    var popUpPercent: Long = 50

    var homeAdsPercent: Long = 70

    var homeAdsGap: Long = 60

    var timeInterstitialAdValid = 3600000L

    var isShowAds = true

    var timeInterstitialWithAppOpenApp = 30000L

    var firstRate = 2

    var laterRate = 6

    private const val BANER_TEST_ADS = "ca-app-pub-3940256099942544/6300978111"

    private const val POPUP_TEST_ADS = "ca-app-pub-3940256099942544/1033173712"

    private const val POPUP_VIDEO_TEST_ADS = "ca-app-pub-3940256099942544/8691691433"

    private const val REWARD_TEST_ADS = "ca-app-pub-3940256099942544/5224354917"

    private const val NATIVE_VIDEO_TEST_ADS = "ca-app-pub-3940256099942544/1044960115"

    private const val NATIVE_TEST_ADS = "ca-app-pub-3940256099942544/2247696110"

    private const val APP_OPEN_ADS_TEST = "ca-app-pub-3940256099942544/3419835294"


    fun getInterstitialAds(): String {
        return if (BuildConfig.DEBUG) {
            POPUP_TEST_ADS
        } else {
            "ca-app-pub-3438626400465865/1474694548"
        }
    }

    fun getRewardedAdsID(): String {
        return if (BuildConfig.DEBUG) {
            REWARD_TEST_ADS
        } else {
            "ca-app-pub-3438626400465865/2480654457"
        }
    }

    fun getNativeVideoAdsId(): String {
        return if (BuildConfig.DEBUG) {
            NATIVE_VIDEO_TEST_ADS
        } else {
            "ca-app-pub-3438626400465865/6918592918"
        }
    }

    fun getBannerAdsId(): String {
        return if (BuildConfig.DEBUG) {
            BANER_TEST_ADS
        } else {
            "ca-app-pub-3438626400465865/9481725812"
        }
    }

    fun getOpenAppAdsId(): String {
        return if (BuildConfig.DEBUG) {
            APP_OPEN_ADS_TEST
        } else {
            "ca-app-pub-3438626400465865/5365003942"
        }
    }

    fun isInterstitialAdInvalid(context: Context): Boolean {
        return System.currentTimeMillis() - PreferencesHelper(context).getInterstitialLoaded() < timeInterstitialAdValid
    }

    fun isShowOpenAd(context: Context): Boolean {
        val preferencesHelper = PreferencesHelper(context)
        return preferencesHelper.isShowStartAd(startAppTimeThreshold) && !MyApplication.getInstance()
            .isFullScreenAds() && !preferencesHelper.isVip() && isValidTimeOpenAdDisplay(context) && isValidTimeAppOpenAdWithInterstitial(
            context
        )
    }

    private fun isValidTimeOpenAdDisplay(context: Context): Boolean {
        return System.currentTimeMillis() - PreferencesHelper(context).getAppOpenAdDisplay() > popupAdsGap
    }

    fun shouldShowInterstitialAd(context: Context): Boolean {
        return isValidTimeInterstitialDisplay(context) && isValidTimeInterstitialAndAppOpenAd(
            context
        ) && isDisplayPercent() && isShowAds && !PreferencesHelper(context).isVipOrVipTrialMember()
    }

    private fun isValidTimeInterstitialDisplay(context: Context): Boolean {
        return System.currentTimeMillis() - PreferencesHelper(context).getInterstitialDisplay() > popupAdsGap
    }

    private fun isValidTimeInterstitialAndAppOpenAd(context: Context): Boolean {
        return System.currentTimeMillis() - PreferencesHelper(context).getAppOpenAdDisplay() > timeInterstitialWithAppOpenApp
    }

    private fun isDisplayPercent(): Boolean {
        return Math.random() < popUpPercent / 100f
    }

    private fun isValidTimeAppOpenAdWithInterstitial(context: Context): Boolean {
        return System.currentTimeMillis() - PreferencesHelper(context).getInterstitialDisplay() > timeInterstitialWithAppOpenApp
    }

}