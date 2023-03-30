package com.example.slide.ui.home

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.GravityCompat
import com.example.slide.R
import com.example.slide.ads.*
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.ActivityMainBinding
import com.example.slide.framework.thirdparty.DialogApp
import com.example.slide.framework.thirdparty.ThirdPartyRequest
import com.example.slide.local.PreferencesHelper
import com.example.slide.repository.DraftRepository
import com.example.slide.ui.create.CreateVideoDialog
import com.example.slide.ui.more.MoreAppActivity
import com.example.slide.ui.my_studio.MyStudioActivity
import com.example.slide.ui.privacy.PrivacyPolicyActivity
import com.example.slide.ui.select_image.SelectActivity
import com.example.slide.ui.setting.SettingActivity
import com.example.slide.ui.video.video_export.RateDialogFragment
import com.example.slide.ui.vip.VipActivity
import com.example.slide.ui.vip.VipMemberActivity
import com.example.slide.util.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.playbilling.BillingListener
import com.playbilling.BillingRepository
import com.playbilling.ProductInfo
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import javax.inject.Inject

var currentNativeAd: NativeAd? = null

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), View.OnClickListener {
    override fun bindingView(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({ R.layout.activity_main })

    companion object {

        private const val TAG = "MainActivity"

        fun getCallingIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    private var adLoadDialog: AdLoadingDialog? = null

    private var isVip = false

    private var PERMISSION_COUNT = 0

    private var currentSelectedViewId = R.id.btn_create_video

    private var mpackage = "com.amazingmusiceffects.musicrecorder.voicechanger"

    private lateinit var preferencesHelper: PreferencesHelper

    override fun onStart() {
        super.onStart()
        initVipState()
    }

    override fun onResume() {
        super.onResume()
        initVipState()
    }

    @Inject
    lateinit var billingRepository: BillingRepository

    override fun initConfiguration(savedInstanceState: Bundle?) {
        super.initConfiguration(savedInstanceState)
        preferencesHelper = PreferencesHelper(this)
        isVip = preferencesHelper.isVip()
        billingRepository.addBillingListener(object : BillingListener{
            override fun onListPurchase(listPurchases: List<String>) {
                super.onListPurchase(listPurchases)
                preferencesHelper.enableVip(listPurchases.isNotEmpty())
                isVip = listPurchases.isNotEmpty()
                initVipState()
            }
        })
        initHomeAdsState()
        loadNativeAds()
    }

    override fun initListener() {
        super.initListener()
        binding.drawer.setOnClickListener(this)

        binding.btnCreateVideo.setOnClickListener(this)
        binding.btnStudio.setOnClickListener(this)
        binding.btnSetting.setOnClickListener(this)

        binding.btnShare.setOnClickListener(this)
        binding.btnRate.setOnClickListener(this)
        binding.btnMore.setOnClickListener(this)
        binding.btnPrivacy.setOnClickListener(this)

        binding.viewMenu.setOnClickListener(this)
        binding.btnVip.setOnClickListener(this)
        binding.btnHomeAd.setOnClickListener(this)
        binding.cardViewSettings.setOnClickListener(this)
    }

    override fun initTask() {
        super.initTask()

        val overtime = preferencesHelper.isOverTime()
        val overHomeAdsGap = preferencesHelper.isOverHomeAdsGap()

        if (overtime
            && ThirdPartyRequest.isLoaded
            && ThirdPartyRequest.is_dialog
            && ThirdPartyRequest.dialogs.isNotEmpty()
            && overHomeAdsGap
            && Math.random() < Ads.homeAdsPercent / 100f
        ) {
            val selectedValue = Math.random()
            var value = 0f
            ThirdPartyRequest.dialogs.forEach {
                if ((value + it.percent / 100f) > selectedValue) {
                    showMoreAppDialog(it)
                    return
                }
                value += (it.percent / 100f)
            }
        } else {
            Timber.d("Overtime: $overtime, OverHomeAdsGap: $overHomeAdsGap")
        }
    }

    private fun initHomeAdsState() {
        if (!isVip && ThirdPartyRequest.is_more_screen && ThirdPartyRequest.apps.isNotEmpty()) {
            binding.layoutHomeAd.visibility = View.VISIBLE
        } else {
            binding.layoutHomeAd.visibility = View.GONE
        }
    }

    private fun showMoreAppDialog(dialog: DialogApp) {
        val bundle = Bundle()
        bundle.putInt("id", dialog.id)
        FirebaseAnalytics.getInstance(this).logEvent("show_dialog", bundle)
        if (dialog.type == ThirdPartyRequest.TYPE_TEXT_DIALOG) {
            Timber.d("Showing text dialog")
            TextDialog.newInstance(dialog).show(supportFragmentManager, "dialog")
        } else {
            ImageDialog.newInstance(dialog).show(supportFragmentManager, "dialog")
            Timber.d("Showing image dialog")
        }

        preferencesHelper.setLastHomeAds(System.currentTimeMillis())
        preferencesHelper.setAppOpenAdDisPlay()
    }

    override fun releaseData() {
        adLoadDialog?.dismiss()
        currentNativeAd?.destroy()
        currentNativeAd = null
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.view_menu -> {
                if (!binding.drawer.isDrawerOpen(GravityCompat.START)) {
                    binding.drawer.openDrawer(GravityCompat.START)
                }
            }
            R.id.btn_create_video -> {
                currentSelectedViewId = R.id.btn_create_video
                if (PermissionUtils.isHavePermission(this)) {
                    val count = DraftRepository(this).getDraftCount()
                    if (count > 0) {
                        CreateVideoDialog.createInstance()
                            .show(supportFragmentManager, CreateVideoDialog.TAG)
                    } else {
                        openActivity(SelectActivity.getIntent(this, SelectActivity.MODE_START))
                    }
                } else {
                    PermissionUtils.requestPermission(this)
                }
            }
            R.id.btn_studio -> {
                currentSelectedViewId = R.id.btn_studio
                if (PermissionUtils.isHavePermission(this)) {
                    openActivity(
                        MyStudioActivity.getCallingIntent(
                            this,
                            MyStudioActivity.VIDEO_MODE
                        )
                    )
                } else {
                    PermissionUtils.requestPermission(this)
                }
            }
            R.id.btn_setting -> {
                openActivity(SettingActivity.getInstance(this))
            }
            R.id.btn_vip -> {
                if (isVip) {
                    startActivity(Intent(this, VipMemberActivity::class.java))
                } else {
                    startActivity(Intent(this, VipActivity::class.java))
                }
            }
            R.id.btn_rate -> {
                rateApp(packageName)
            }
            R.id.btn_share -> {
                ShareUtils.shareApp(this)
            }
            R.id.btn_more -> {
                val id = "6035727937838970967"
                try {
                    startActivity(
                        Intent(
                            "android.intent.action.VIEW",
                            Uri.parse("market://dev?id=$id")
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            "android.intent.action.VIEW",
                            Uri.parse("https://play.google.com/store/apps/dev?id=$id")
                        )
                    )
                }
            }
            R.id.btn_privacy -> {
                startActivity(PrivacyPolicyActivity.getInstance(this))
            }
            R.id.btn_home_ad -> {
                startActivity(MoreAppActivity.getCallingIntent(this))
            }
        }
    }

    fun goToAppFromDialog(dialog: DialogApp) {

        val bundle = Bundle()
        bundle.putInt("id", dialog.id)
        FirebaseAnalytics.getInstance(this).logEvent("dialog_clicked", bundle)
        val uri = Uri.parse("market://details?id=${dialog.vPackage}")
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
                    Uri.parse("http://play.google.com/store/apps/details?id=${dialog.vPackage}")
                )
            )
        }
    }

    private fun initVipState() {
        isVip = PreferencesHelper(this).isVip()
        if (isVip) {
            binding.layoutAdsParent.visibility = View.INVISIBLE
        } else {
            binding.layoutAdsParent.visibility = View.VISIBLE
        }
    }

    private fun openActivity(intent: Intent) {
        InterstitialHelperV2.showInterstitialAd(this, object : OnInterstitialCallback {
            override fun onWork() {
                startActivity(intent)
            }

        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionUtils.REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    if (!Utils.isScopeStorage()) {
                        File(MyStatic.FOLDER_SHARK_VIDEO).mkdirs()
                    }
                    when (currentSelectedViewId) {
                        R.id.btn_create_video -> {
                            openActivity(SelectActivity.getIntent(this, SelectActivity.MODE_START))
                        }
                        R.id.btn_studio -> {
                            openActivity(
                                MyStudioActivity.getCallingIntent(
                                    this,
                                    MyStudioActivity.VIDEO_MODE
                                )
                            )
                        }
                    }
                } else {
                    PermissionsDialogFragment.getInstance().show(supportFragmentManager, TAG)
                }
            }
        }
    }

    private fun loadNativeAds() {
        if (isVip) return
        val builder = AdLoader.Builder(this, Ads.getNativeVideoAdsId())
        builder.forNativeAd { nativeAds ->
            // OnUnifiedNativeAdLoadedListener implementation.
            // If this callback occurs after the activity is destroyed, you must call
            // destroy and return or you may get a memory leak.
            if (isDestroyed || isFinishing || isChangingConfigurations) {
                nativeAds.destroy()
                return@forNativeAd
            }
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            currentNativeAd?.destroy()
            currentNativeAd = nativeAds
            val adView = layoutInflater.inflate(R.layout.item_ads_1, null) as NativeAdView
            populateUnifiedNativeAdView(nativeAds, adView)
            binding.layoutAdsParent.removeAllViews()
            binding.layoutAdsParent.addView(adView)
        }
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateUnifiedNativeAdView(
        nativeAd: NativeAd,
        adView: NativeAdView
    ) {
        // Set the media view.
        adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView?.mediaContent = nativeAd.mediaContent

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView?.visibility = View.INVISIBLE
        } else {
            adView.priceView?.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView?.visibility = View.INVISIBLE
        } else {
            adView.storeView?.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView?.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView?.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView?.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView?.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
    }
}