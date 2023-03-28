package com.example.slide.local

import android.content.Context
import android.content.SharedPreferences
import com.example.slide.BuildConfig
import com.example.slide.ads.Ads
import com.example.slide.util.AppConfig
import com.example.slide.util.MyStatic

class PreferencesHelper(context: Context) {

    companion object {

        const val APP_PREF = "app_pref"

        const val KEY_IMAGE_ID = "image_id"

        const val KEY_DURATION_ID = "duration_id"

        const val KEY_TERM_AND_CONDITION_ID = "term_and_condition_id"

        const val KEY_IS_APP_RATED = "app_rated"

        const val KEY_RATE_APP_SESSION = "rate_app_session"

        const val KEY_NEVER_RATE_APP = "never_rate_app_id"

        const val KEY_CURRENT_TIME_ID = "current_time_id"

        const val KEY_RATE_LATER = "rate_later"

        private const val KEY_IS_VIP_MONTHLY = "vip_monthly"

        private const val KEY_IS_VIP_YEARLY = "vip_yearly"

        const val KEY_VIP_TRAIL = "vip_trail"

        const val KEY_LAST_TIME_SHOW_ADS = "last_ads_time"

        const val KEY_FIRST_TIME_OPEN_APP = "first_time_open_app"

        const val KEY_RELOAD_INTERSTITIAL_AD = "reload_interstitial_ad"

        const val KEY_INTERSTITIAL_AD_LOADED = "interstitial_ad_loaded"

        const val OPEN_APP_FIRST_TIME = "open_app_first_time"

        const val APP_OPEN_AD_DISPLAY = "app_open_ad_display"

        const val INTERSTITIAL_DISPLAY = "interstitial_display"

        const val KEY_TIME = "key_time"

        const val KEY_LAST_HOME_ADS = "last_home_ads"

        const val KEY_VIP = "key_vip"

        const val EXPORT_FILE = "export_file"

        const val FIRST_RATE = "first_rate"
    }

    private val appSharedPreferences = context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE)

    fun isExportShowRate(rateExportThreshold: Int): Boolean {
        val editor = appSharedPreferences.edit()
        val numberRate = appSharedPreferences.getInt(EXPORT_FILE, 1)
        val isShow = numberRate == rateExportThreshold
        if (isShow) {
            editor.putInt(EXPORT_FILE, 1).apply()
        } else {
            editor.putInt(EXPORT_FILE, appSharedPreferences.getInt(EXPORT_FILE, 1) + 1).apply()
        }
        return isShow
    }

    fun isNeverRateApp(): Boolean {
        return appSharedPreferences.getBoolean(KEY_NEVER_RATE_APP, false)
    }

    fun neverRateApp() {
        appSharedPreferences.edit().putBoolean(KEY_NEVER_RATE_APP, true).apply()
    }

    fun isFirstRate(): Boolean {
        return appSharedPreferences.getBoolean(FIRST_RATE, false)
    }

    fun setFirstRate() {
        appSharedPreferences.edit().putBoolean(FIRST_RATE, true).apply()
    }

    fun setOpenFistTime() {
        appSharedPreferences.edit().putLong(OPEN_APP_FIRST_TIME, System.currentTimeMillis()).apply()
    }

    fun isOverTime(): Boolean {
        val time: Int =
            appSharedPreferences.getInt(KEY_TIME, 0)
        if (time < 3) {
            appSharedPreferences.edit().putInt(KEY_TIME, time + 1).apply()
            return false
        }
        return true
    }

    fun isOverHomeAdsGap(): Boolean {
        return System.currentTimeMillis() - appSharedPreferences.getLong(
            KEY_LAST_HOME_ADS,
            0
        ) > Ads.homeAdsGap
    }

    fun setLastHomeAds(time: Long) {
        appSharedPreferences.edit().putLong(KEY_LAST_HOME_ADS, time).apply()
    }

    fun isShowStartAd(timeThreshold: Long): Boolean {
        return System.currentTimeMillis() - appSharedPreferences.getLong(
            OPEN_APP_FIRST_TIME,
            0
        ) >= timeThreshold
    }

    fun getSharedPreferences(): SharedPreferences {
        return appSharedPreferences
    }

    fun setReloadInterstitialTime() {
        appSharedPreferences.edit().putLong(KEY_RELOAD_INTERSTITIAL_AD, System.currentTimeMillis())
            .apply()
    }

    fun getReloadInterstitialTime(): Long {
        return appSharedPreferences.getLong(KEY_RELOAD_INTERSTITIAL_AD, 0)
    }

    fun setInterstitialLoaded() {
        appSharedPreferences.edit().putLong(KEY_INTERSTITIAL_AD_LOADED, System.currentTimeMillis())
            .apply()
    }

    fun getInterstitialLoaded(): Long {
        return appSharedPreferences.getLong(KEY_INTERSTITIAL_AD_LOADED, System.currentTimeMillis())
    }

    fun saveKeyImageId(id: Float) {
        if (id < 10000F)
            appSharedPreferences.edit().putFloat(KEY_IMAGE_ID, id).apply()
        else
            appSharedPreferences.edit().putFloat(KEY_IMAGE_ID, 0F).apply()
    }

    fun getKeyImageId(): Float {
        return appSharedPreferences.getFloat(KEY_IMAGE_ID, 0f)
    }

    fun saveDuration(duration: Int) {
        appSharedPreferences.edit().putInt(KEY_DURATION_ID, duration).apply()
    }

    fun getDuration(): Int {
        return appSharedPreferences.getInt(KEY_DURATION_ID, 2)
    }

    fun saveTermAndConditionMode(isAllowTermsAndConditions: Boolean) {
        appSharedPreferences.edit().putBoolean(KEY_TERM_AND_CONDITION_ID, isAllowTermsAndConditions)
            .apply()
    }

    fun isAllowTermsAndConditions(): Boolean {
        return appSharedPreferences.getBoolean(KEY_TERM_AND_CONDITION_ID, false)
    }

    fun setAppRated() {
        appSharedPreferences.edit().putBoolean(KEY_IS_APP_RATED, true).apply()
    }

    fun isAppRated(): Boolean {
        return appSharedPreferences.getBoolean(KEY_IS_APP_RATED, false)
    }

    fun isPassThresholdTime(): Boolean {
        val lastTime = appSharedPreferences.getLong(KEY_CURRENT_TIME_ID, 0L)
        return System.currentTimeMillis() - lastTime > MyStatic.RATE_LATER_TIME_THRESHOLD
    }

    fun rateLater() {
        appSharedPreferences.edit().putBoolean(KEY_RATE_LATER, true).apply()
    }

    fun isRateLate(): Boolean {
        return appSharedPreferences.getBoolean(KEY_RATE_LATER, false)
    }

    fun isVip(): Boolean {
        return appSharedPreferences.getBoolean(KEY_VIP, false) || appSharedPreferences.getBoolean(
            KEY_IS_VIP_MONTHLY,
            false
        ) || appSharedPreferences.getBoolean(
            KEY_IS_VIP_YEARLY, false
        )
    }

    fun enableVip(isVip: Boolean) {
        appSharedPreferences.edit().putBoolean(KEY_VIP, isVip).apply()
    }

    fun enableVipMonthly(isVip: Boolean) {
        appSharedPreferences.edit().putBoolean(KEY_IS_VIP_MONTHLY, isVip).apply()
    }

    fun enableVipYearly(isVip: Boolean) {
        appSharedPreferences.edit().putBoolean(KEY_IS_VIP_YEARLY, isVip).apply()
    }

    fun isVipOrVipTrialMember(): Boolean {
        return isVip() || isVipTrail()
    }

    fun enableVipTrail() {
        appSharedPreferences.edit().putLong(KEY_VIP_TRAIL, System.currentTimeMillis()).apply()
    }

    fun isVipTrail(): Boolean {
        val lastTime = appSharedPreferences.getLong(KEY_VIP_TRAIL, 0)
        return System.currentTimeMillis() - lastTime < MyStatic.ONE_HOUR_IN_MILLIS
    }

    fun setFirstTime() {
        val firstTime = appSharedPreferences.getLong(KEY_FIRST_TIME_OPEN_APP, 0)
        if (firstTime == 0L)
            appSharedPreferences.edit().putLong(KEY_FIRST_TIME_OPEN_APP, System.currentTimeMillis())
                .apply()
    }

    fun getTrailTime(): Long {
        val lastTime = appSharedPreferences.getLong(KEY_VIP_TRAIL, 0)
        return MyStatic.ONE_HOUR_IN_MILLIS - System.currentTimeMillis() + lastTime
    }

    fun isOverThresholdSession(threshold: Int): Boolean {
        val session: Int =
            appSharedPreferences.getInt(KEY_RATE_APP_SESSION, 0)
        if (session < threshold) {
            appSharedPreferences.edit().putInt(KEY_RATE_APP_SESSION, session + 1).apply()
            return false
        }
        return true
    }

    fun isOverThresholdTime(): Boolean {
        val firstTime = appSharedPreferences.getLong(KEY_FIRST_TIME_OPEN_APP, 0)
        return System.currentTimeMillis() - firstTime > AppConfig.rateTimeThreshold
    }

    fun getAppOpenAdDisplay(): Long {
        return appSharedPreferences.getLong(APP_OPEN_AD_DISPLAY, 0)
    }

    fun setAppOpenAdDisPlay() {
        appSharedPreferences.edit()
            .putLong(APP_OPEN_AD_DISPLAY, System.currentTimeMillis()).apply()
    }

    fun getInterstitialDisplay(): Long {
        return appSharedPreferences.getLong(INTERSTITIAL_DISPLAY, 0)
    }

    fun setInterstitialDisplay() {
        appSharedPreferences.edit()
            .putLong(INTERSTITIAL_DISPLAY, System.currentTimeMillis()).apply()
    }

}