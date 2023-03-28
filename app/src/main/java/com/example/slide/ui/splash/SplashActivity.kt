package com.example.slide.ui.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.example.slide.MyApplication
import com.example.slide.R
import com.example.slide.ads.Ads
import com.example.slide.event.AppOpenAdsCloseEvent
import com.example.slide.framework.thirdparty.ThirdPartyRequest
import com.example.slide.local.PreferencesHelper
import com.example.slide.notify.NotificationRepository
import com.example.slide.ui.home.MainActivity
import com.example.slide.ui.invalidinstall.InvalidInstalledActivity
import com.example.slide.util.AppConfig
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    companion object {

        private const val NOTIFICATION_SETUP_H = "notification_setup_h"

        private const val NOTIFICATION_SETUP_M = "notification_setup_m"

        private const val NOTIFICATION_CONTENT = "notification_content"

        const val FIRST_TIME_APP_OPEN_ADS = "first_time_app_open_ads"

        const val KEY_ADS_GAP = "popup_gap"

        const val KEY_ADS_PERCENT = "popup_percent"

        const val KEY_HOME_ADS_GAP = "home_ads_gap"

        const val KEY_HOME_ADS_PERCENT = "home_ads_percent"

        const val START_APP_TIME_THRESHOLD = "start_app_time_threshold"

        const val TIME_INTERSTITIAL_AD_VALID = "time_interstitial_ad_valid"

        const val TIME_INTERSTITIAL_WITH_APP_OPEN_APP = "time_interstitial_with_app_open_app"

        const val IS_SHOW_ADS = "is_show_ads"

        const val KEY_FIRST_RATE = "first_rate"

        const val KEY_LATER_RATE = "later_rate"

        //@deprecated
        const val KEY_SHOW_RATE_IN_MAIN = "show_rate_in_main"

        const val KEY_SHOW_RATE_USING_SESSION = "show_rate_using_session"

        const val KEY_RATE_TIME_THRESHOLD = "rate_time_threshold"

        const val KEY_RATE_MAIN_SESSION_THRESHOLD = "rate_main_session_threshold"

        const val KEY_RATE_EXPORT_SESSION_THRESHOLD = "rate_export_session_threshold"

        private const val SPLASH_DISPLAY_LENGTH: Long = 2500
    }

    @Inject
    lateinit var notificationRepository: NotificationRepository

    private var isValidInstalled = true

    private val handler: Handler by lazy {
        Handler(mainLooper)
    }

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(this)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isValidInstalled = isValidInstallation()
        Log.d("kimkaka1","isValidInstalled"+isValidInstalled)
        if(!isValidInstalled) {
            FirebaseAnalytics.getInstance(this).logEvent("invalid_install", null)
            val intent = Intent(this, InvalidInstalledActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        setContentView(R.layout.activity_splash)
        EventBus.getDefault().register(this)
        initConfiguration()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        Log.d("kimkaka1","onStart"+isValidInstalled)
        if(!isValidInstalled) return
        handler.postDelayed(
            {
                if (!(application as MyApplication).appOpenManager.isShowingAd) {
                    splashDone()
                }
            },
            SPLASH_DISPLAY_LENGTH
        )
    }

    private fun splashDone() {
        if (!preferencesHelper.isAllowTermsAndConditions()) {
            finish()
            startActivity(WelcomeActivity.getInstance(this))
        } else {
            finish()
            startActivity(MainActivity.getCallingIntent(this))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAppOpenAdsClose(appOpenAdsCloseEvent: AppOpenAdsCloseEvent) {
        splashDone()
    }

    private fun initConfiguration() {
        MobileAds.initialize(this) {}
        PreferencesHelper(this).setFirstTime()
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.default_config)
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            Log.d("kimkakakconfig", "fetch")
            if (task.isSuccessful) {
                Ads.startAppTimeThreshold = remoteConfig.getLong(FIRST_TIME_APP_OPEN_ADS)
                Ads.popupAdsGap = remoteConfig.getLong(KEY_ADS_GAP)
                Ads.popUpPercent = remoteConfig.getLong(KEY_ADS_PERCENT)
                Ads.homeAdsGap = remoteConfig.getLong(KEY_HOME_ADS_GAP)
                Ads.homeAdsPercent = remoteConfig.getLong(KEY_HOME_ADS_PERCENT)

                Ads.startAppTimeThreshold = remoteConfig.getLong(START_APP_TIME_THRESHOLD)
                Ads.timeInterstitialAdValid = remoteConfig.getLong(TIME_INTERSTITIAL_AD_VALID)
                Ads.timeInterstitialWithAppOpenApp =
                    remoteConfig.getLong(TIME_INTERSTITIAL_WITH_APP_OPEN_APP)
                Ads.isShowAds = remoteConfig.getBoolean(IS_SHOW_ADS)


                AppConfig.isShowRateInMain = remoteConfig.getBoolean(KEY_SHOW_RATE_IN_MAIN)
                AppConfig.isShowRateUsingSession =
                    remoteConfig.getBoolean(KEY_SHOW_RATE_USING_SESSION)
                AppConfig.rateTimeThreshold = remoteConfig.getLong(KEY_RATE_TIME_THRESHOLD)
                AppConfig.rateMainSessionThreshold =
                    remoteConfig.getLong(KEY_RATE_MAIN_SESSION_THRESHOLD)
                AppConfig.rateExportSessionThreshold =
                    remoteConfig.getLong(KEY_RATE_EXPORT_SESSION_THRESHOLD)


                Ads.firstRate = remoteConfig.getLong(KEY_FIRST_RATE).toInt()
                Ads.laterRate = remoteConfig.getLong(KEY_LATER_RATE).toInt()

                Ads.notification_setup_h =
                    remoteConfig.getLong(NOTIFICATION_SETUP_H).toInt()
                Ads.notification_setup_m =
                    remoteConfig.getLong(NOTIFICATION_SETUP_M).toInt()
                Ads.notification_content =
                    remoteConfig.getLong(NOTIFICATION_CONTENT).toInt()
                notificationRepository.apply {
                    setNotificationTime(Ads.notification_setup_h, Ads.notification_setup_m)
                    setRemoteNotificationContent(Ads.notification_content)
                }
            }
        }

        Ads.popupAdsGap = remoteConfig.getLong(KEY_ADS_GAP)
        Ads.popUpPercent = remoteConfig.getLong(KEY_ADS_PERCENT)
        Ads.homeAdsGap = remoteConfig.getLong(KEY_HOME_ADS_GAP)
        Ads.homeAdsPercent = remoteConfig.getLong(KEY_HOME_ADS_PERCENT)
        AppConfig.isShowRateInMain = remoteConfig.getBoolean(KEY_SHOW_RATE_IN_MAIN)
        AppConfig.isShowRateUsingSession = remoteConfig.getBoolean(KEY_SHOW_RATE_USING_SESSION)
        AppConfig.rateTimeThreshold = remoteConfig.getLong(KEY_RATE_TIME_THRESHOLD)
        AppConfig.rateMainSessionThreshold = remoteConfig.getLong(KEY_RATE_MAIN_SESSION_THRESHOLD)
        AppConfig.rateExportSessionThreshold =
            remoteConfig.getLong(KEY_RATE_EXPORT_SESSION_THRESHOLD)

        ThirdPartyRequest.getMoreApp(applicationContext)

        Glide.with(this)
            .load(Uri.parse("file:///android_asset/play.gif"))
            .into(iv_play)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }

    private fun isValidInstallation(): Boolean {
        return try {
            val logo = ResourcesCompat.getDrawable(resources, R.drawable.bg_splash, null)
            logo != null
        } catch (e: Exception) {
            false
        }
    }
}