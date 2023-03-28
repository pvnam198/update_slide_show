package com.example.slide.ui.vip

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.photo_video_maker_with_song.network.NetworkHelper
import com.example.slide.R
import com.example.slide.ads.AdLoadingDialog
import com.example.slide.ads.OnRewardAdCallback
import com.example.slide.ads.RewardHelper
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.example.slide.local.PreferencesHelper
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.util.BillingConstants.MONTHLY
import com.example.slide.util.BillingConstants.YEARLY
import com.example.slide.util.Utils
import com.google.android.gms.ads.rewarded.RewardItem
import com.playbilling.BillingListener
import com.playbilling.BillingRepository
import com.playbilling.ProductInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_sub.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class VipActivity : BaseActivity(), View.OnClickListener, OnRewardAdCallback {

    override fun initViewTools() = InitViewTools({
        R.layout.activity_sub
    })

    companion object {

        const val TAG = "VipActivity123"

        const val ARG_REQUEST_FROM_VIDEO_CREATE_ACTIVITY = "request from video create activity"

        private const val COUNT_DOWN_INTERVAL = 1000L

        fun getIntent(context: Context): Intent {
            return Intent(context, VipActivity::class.java)
        }

        fun getIntent(context: Context, activityName: String): Intent {
            val intent = Intent(context, VipActivity::class.java)
            intent.putExtra(ARG_REQUEST_FROM_VIDEO_CREATE_ACTIVITY, activityName)
            return intent
        }
    }

    private lateinit var sharedPref: PreferencesHelper

    private var activityName = VipActivity::class.java.name

    @Inject
    lateinit var billingRepository: BillingRepository

    private val vipTrialTimer = Timer()

    private var adLoadingDialog: AdLoadingDialog? = null

    private val vipTrialCountDownRunnable = object : TimerTask() {
        override fun run() {
            lifecycleScope.launchWhenResumed {
                val timeRemaining = PreferencesHelper(this@VipActivity).getTrailTime()
                if (PreferencesHelper(this@VipActivity).isVipTrail()) {
                    tv_watch_ads.visibility = View.GONE
                    vip_trial_countdown.visibility = View.VISIBLE
                    tv_count_down_time.text = Utils.convertingMillisecondsToHours(timeRemaining)
                } else {
                    tv_watch_ads.visibility = View.VISIBLE
                    vip_trial_countdown.visibility = View.GONE
                }
            }
        }
    }

    override fun extractData(bundle: Bundle) {
        super.extractData(bundle)
        activityName = bundle.getString(ARG_REQUEST_FROM_VIDEO_CREATE_ACTIVITY).toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ARG_REQUEST_FROM_VIDEO_CREATE_ACTIVITY, activityName)
    }

    override fun initConfiguration(savedInstanceState: Bundle?) {
        super.initConfiguration(savedInstanceState)
        sharedPref = PreferencesHelper(this)
        tv_manager_subscriptions.movementMethod = LinkMovementMethod.getInstance()
        if (sharedPref.isVip()) {
            updateVipUI()
        } else {
            updateBillingUI()
        }
    }

    private fun updateVipUI() {
        vipTrialTimer.cancel()
        layout_vip.visibility = View.VISIBLE
        layout_buy_vip.visibility = View.GONE
    }

    private fun updateBillingUI() {
        billingRepository.addBillingListener(object : BillingListener {

            override fun onListPurchase(listPurchases: List<String>) {
                super.onListPurchase(listPurchases)
                sharedPref.enableVip(listPurchases.isNotEmpty())
                lifecycleScope.launchWhenResumed {
                    if (listPurchases.isNotEmpty()) {
                        updateVipUI()
                    }
                }
            }

            override fun onListProductDetails(listProductDetails: List<ProductInfo>) {
                super.onListProductDetails(listProductDetails)
                loading.visibility = View.GONE
                lifecycleScope.launchWhenResumed {
                    if (listProductDetails.isEmpty()) {
                        layout_billing.visibility = View.GONE
                        layout_error.visibility = View.VISIBLE
                    } else {
                        layout_billing.visibility = View.VISIBLE
                        layout_error.visibility = View.GONE
                        listProductDetails.find { it.getProductId() == MONTHLY }?.let {
                            tv_price_monthly.text = it.getFormatPrice()
                        }
                        listProductDetails.find { it.getProductId() == YEARLY }?.let {
                            tv_price_yearly.text = it.getFormatPrice()
                        }
                    }
                }
            }

            override fun onBillingSuccess(billingProduct: ProductInfo?) {
                super.onBillingSuccess(billingProduct)
                sharedPref.enableVip(true)
                lifecycleScope.launchWhenResumed {
                    updateVipUI()
                    VipMemberDialogFragment.show(supportFragmentManager)
                    Toast.makeText(
                        this@VipActivity,
                        R.string.msg_vip_granted,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onBillingSetupFailed() {
                super.onBillingSetupFailed()
                lifecycleScope.launchWhenResumed {
                    loading.visibility = View.GONE
                    layout_billing.visibility = View.GONE
                    layout_error.visibility = View.VISIBLE
                }
            }

            override fun onBillingError() {
                super.onBillingError()
                lifecycleScope.launchWhenResumed {
                    Toast.makeText(
                        this@VipActivity,
                        R.string.msg_purchase_error,
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

            override fun onUserCancel() {
                super.onUserCancel()
                lifecycleScope.launchWhenResumed {
                    BecomeVipDialogFragment.newInstance()
                        .show(supportFragmentManager, BecomeVipDialogFragment.TAG)
                }
            }

            override fun onItemAlreadyOwned() {
                super.onItemAlreadyOwned()
                lifecycleScope.launchWhenResumed { updateVipUI() }
            }

        })
        RewardHelper.initFullScreenContentCallback()
        vipTrialTimer.scheduleAtFixedRate(vipTrialCountDownRunnable, 0, COUNT_DOWN_INTERVAL)
    }

    private fun completeViewingAdvertisement() {
        Toast.makeText(this, getString(R.string.msg_thank_you), Toast.LENGTH_SHORT).show()
        if (activityName == VideoCreateActivity.ACTIVITY_NAME) {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun initListener() {
        super.initListener()
        btn_cancel.setOnClickListener(this)
        btn_one_hour.setOnClickListener(this)
        btn_watch_ads.setOnClickListener(this)
        btn_purchase_monthly.setOnClickListener(this)
        btn_purchase_yearly.setOnClickListener(this)
        RewardHelper.onRewardAdCallback = this
        btn_reload.setOnClickListener {
            connectToPlayBillingService()
        }
    }

    override fun releaseData() {
        RewardHelper.onRewardAdCallback = null
        vipTrialTimer.cancel()
    }

    override fun onClick(view: View) {
        when (view) {
            btn_cancel -> onBackPressed()
            btn_purchase_monthly -> {
                billingRepository.buy(this, MONTHLY)
            }
            btn_purchase_yearly -> {
                billingRepository.buy(this, YEARLY)
            }
            btn_watch_ads -> {
                if (!PreferencesHelper(this).isVipOrVipTrialMember()) {
                    if (NetworkHelper.isConnected) {
                        adLoadingDialog = AdLoadingDialog.getInstance()
                        adLoadingDialog?.show(supportFragmentManager, AdLoadingDialog.TAG)
                        RewardHelper.loadRewardedAd(this)
                    } else {
                        showToast(getString(R.string.no_internet))
                    }
                }
            }
        }
    }

    private fun dismissAdLoading() {
        if (canChangeFragmentManagerState()) {
            adLoadingDialog?.dismiss()
            adLoadingDialog = null
        }
    }

    fun tryPurchaseAgain() {
        loading.visibility = View.VISIBLE
        billingRepository.reconnect()
    }

    override fun onAdShowedFullScreenContent() {
    }

    override fun onAdDismissedFullScreenContent(rewardItemFromUser: Boolean) {
        if (rewardItemFromUser) {
            completeViewingAdvertisement()
        } else {
            showToast(getString(R.string.msg_please_watch_ads))
        }
    }

    override fun onUserEarnedReward(rewardItem: RewardItem) {
        PreferencesHelper(this@VipActivity).enableVipTrail()
    }

    override fun onAdLoading() {
    }

    override fun onAdLoaded() {
        lifecycleScope.launchWhenResumed {
            dismissAdLoading()
            RewardHelper.showRewardAd(this@VipActivity)
        }
    }

    override fun onAdFailedLoad() {
        lifecycleScope.launchWhenResumed {
            dismissAdLoading()
            showToast(R.string.msg_cant_load_ads)
        }
    }

    fun connectToPlayBillingService() {
        loading.visibility = View.VISIBLE
        billingRepository.reconnect()
    }

}