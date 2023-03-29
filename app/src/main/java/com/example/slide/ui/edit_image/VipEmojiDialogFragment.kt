package com.example.slide.ui.edit_image

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.example.photo_video_maker_with_song.network.NetworkHelper
import com.example.slide.R
import com.example.slide.ads.AdLoadingDialog
import com.example.slide.ads.OnRewardAdCallback
import com.example.slide.ads.RewardHelper
import com.example.slide.base.BaseBindingDialog
import com.example.slide.databinding.DialogEmojiVipFunctionsBinding
import com.example.slide.local.PreferencesHelper
import com.example.slide.ui.vip.VipActivity
import com.google.android.gms.ads.rewarded.RewardItem

class VipEmojiDialogFragment : BaseBindingDialog<DialogEmojiVipFunctionsBinding>(), View.OnClickListener, OnRewardAdCallback {

    var isViewCreated = false

    private var adLoadingDialog: AdLoadingDialog? = null

    companion object {
        const val TAG: String = "VipEmojiDialogFragment"

        fun createInstance(): VipEmojiDialogFragment {
            return VipEmojiDialogFragment()
        }
    }

    override val layoutId: Int = R.layout.dialog_emoji_vip_functions
    override fun bindingView(): DialogEmojiVipFunctionsBinding {
        return DialogEmojiVipFunctionsBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("kimkaka1", "onViewCreated")
        isViewCreated = true
        RewardHelper.initFullScreenContentCallback()
        RewardHelper.onRewardAdCallback = this
        binding.btnWatchAds.setOnClickListener(this)
        binding.btnBecomeVip.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
    }

    override fun onDestroyView() {
        Log.d("kimkaka1", "onDestroyView")
        super.onDestroyView()
        adLoadingDialog = null
        RewardHelper.onRewardAdCallback = null
        isViewCreated = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return dialog
    }

    private fun loadRewardedAds() {
        if (!PreferencesHelper(requireContext()).isVipOrVipTrialMember()) {
            if (NetworkHelper.isConnected) {
                adLoadingDialog = AdLoadingDialog.getInstance()
                adLoadingDialog?.show(parentFragmentManager, AdLoadingDialog.TAG)
                RewardHelper.loadRewardedAd(requireActivity())
            } else {
                Toast.makeText(requireContext(), R.string.no_internet, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), R.string.msg_thank_you, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnCancel -> {
                dismiss()
            }
            binding.btnWatchAds -> {
                loadRewardedAds()
            }
            binding.btnBecomeVip -> {
                startActivity(Intent(requireActivity(), VipActivity::class.java))
            }
        }
    }

    override fun onAdLoading() {
    }

    override fun onAdLoaded() {
        if (canChangeFragmentManagerState())
            dismissAdLoading()
        RewardHelper.showRewardAd(requireActivity())
    }

    override fun onAdFailedLoad() {
        if (canChangeFragmentManagerState())
            dismissAdLoading()
        Toast.makeText(requireContext(), R.string.msg_cant_load_ads, Toast.LENGTH_SHORT).show()
    }

    override fun onAdShowedFullScreenContent() {
    }

    override fun onAdDismissedFullScreenContent(rewardItemFromUser: Boolean) {
        if (rewardItemFromUser) {
            Toast.makeText(requireContext(), getString(R.string.msg_thank_you), Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(requireContext(), R.string.msg_please_watch_ads, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onUserEarnedReward(rewardItem: RewardItem) {
        PreferencesHelper(requireActivity()).enableVipTrail()
    }

    private fun dismissAdLoading() {
        adLoadingDialog?.dismiss()
        adLoadingDialog = null
    }

}