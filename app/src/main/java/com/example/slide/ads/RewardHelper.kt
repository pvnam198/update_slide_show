package com.example.slide.ads

import android.app.Activity
import com.example.slide.MyApplication
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


object RewardHelper {

    private var rewardedAd: RewardedAd? = null

    var onRewardAdCallback: OnRewardAdCallback? = null

    private var rewardItemFromUser = false

    private var fullScreenContentCallback: FullScreenContentCallback? = null

    fun initFullScreenContentCallback() {
        fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    MyApplication.getInstance().setIsFullScreenAds(true)
                    onRewardAdCallback?.onAdShowedFullScreenContent()
                }

                override fun onAdDismissedFullScreenContent() {
                    MyApplication.getInstance().setIsFullScreenAds(false)
                    onRewardAdCallback?.onAdDismissedFullScreenContent(rewardItemFromUser)
                    rewardedAd = null
                }
            }
    }

    fun loadRewardedAd(activity: Activity) {
        onRewardAdCallback?.onAdLoading()
        RewardedAd.load(
            activity,
            Ads.getRewardedAdsID(),
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    super.onAdLoaded(rewardedAd)
                    this@RewardHelper.rewardedAd = rewardedAd
                    this@RewardHelper.rewardedAd!!.fullScreenContentCallback = fullScreenContentCallback
                    onRewardAdCallback?.onAdLoaded()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    onRewardAdCallback?.onAdFailedLoad()
                }
            })
    }

    fun showRewardAd(activity: Activity) {
        rewardedAd?.show(
            activity
        ) { rewardItem ->
            rewardItemFromUser = true
            onRewardAdCallback?.onUserEarnedReward(rewardItem)
        }
    }

}