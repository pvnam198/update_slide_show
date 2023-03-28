package com.example.slide.util

object BillingConstants {
    const val VIP_SKU = "vip_version"

    const val MONTHLY = "monthly_user"
    const val YEARLY = "yearly_user"

    val IN_APP = arrayListOf(VIP_SKU)
    val SUBS = arrayListOf(MONTHLY, YEARLY)

    const val PLAY_STORE_SUBSCRIPTION_URL
            = "https://play.google.com/store/account/subscriptions"
    const val PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL
            = "https://play.google.com/store/account/subscriptions?sku=%s&package=%s"
}