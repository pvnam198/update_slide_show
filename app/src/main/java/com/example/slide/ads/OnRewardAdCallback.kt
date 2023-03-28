package com.example.slide.ads

import com.google.android.gms.ads.rewarded.RewardItem

interface OnRewardAdCallback {
    fun onAdLoading()
    fun onAdLoaded()
    fun onAdFailedLoad()
    fun onAdShowedFullScreenContent()
    fun onAdDismissedFullScreenContent(rewardItemFromUser: Boolean)
    fun onUserEarnedReward(rewardItem: RewardItem)
}