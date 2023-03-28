package com.example.slide

import android.app.Application
import com.example.photo_video_maker_with_song.network.INetworkManager
import com.example.photo_video_maker_with_song.network.NetworkHelper
import com.example.slide.ads.AppOpenManager
import com.example.slide.ads.InterstitialHelperV2
import com.example.slide.local.PreferencesHelper
import com.example.slide.notify.NotificationRepository
import com.example.slide.notify.NotificationSetter
import com.example.slide.repository.DraftRepository
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider
import com.example.slide.util.MyStatic
import com.example.slide.util.Utils
import com.example.slide.videolib.VideoConfig
import com.example.slide.videolib.VideoDataState
import com.google.android.gms.ads.MobileAds
import com.playbilling.BillingListener
import com.playbilling.BillingRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.io.File
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), INetworkManager {

    lateinit var appOpenManager: AppOpenManager

    @Inject
    lateinit var notificationSetter: NotificationSetter

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var billingRepository: BillingRepository

    val videoDataState = VideoDataState()

    private var isFullScreenAds = false

    fun setIsFullScreenAds(isFullScreenAds: Boolean) {
        this.isFullScreenAds = isFullScreenAds
    }

    fun isFullScreenAds(): Boolean {
        return isFullScreenAds
    }

    companion object {

        private const val TAG = "MyApplication"

        private lateinit var instance: MyApplication

        fun getInstance(): MyApplication {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        billingRepository.addBillingListener(object : BillingListener {
            override fun onListPurchase(listPurchases: List<String>) {
                super.onListPurchase(listPurchases)
                PreferencesHelper(this@MyApplication).enableVip(listPurchases.isNotEmpty())
            }
        })
        GlobalScope.launch {
            DraftRepository(instance).deleteInvalidDraft()
        }
        NetworkHelper.iNetworkManager = this
        NetworkHelper.startNetworkCallback(instance)
        initAds()
        LocalMusicProvider.createInstance(this)
        if (!Utils.isScopeStorage()) {
            File(MyStatic.FOLDER_SHARK_VIDEO).mkdirs()
        }
        Timber.plant(DebugTree())
        val preferencesHelper = PreferencesHelper(this)
        VideoConfig.initSecondPerImageAnimate(preferencesHelper.getDuration())
        setNotify()
    }

    private fun setNotify() {
        if (notificationRepository.shouldCreateNewNotification())
            notificationSetter.schedule(this)
    }

    private fun initAds() {
        MobileAds.initialize(this) {}
        appOpenManager = AppOpenManager(this)
        CoroutineScope(Dispatchers.Main).launch {
            InterstitialHelperV2.loadAd(this@MyApplication)
        }
    }

    override fun onNetworkChanged(isNetwork: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            InterstitialHelperV2.loadAd(this@MyApplication)
        }
    }

}