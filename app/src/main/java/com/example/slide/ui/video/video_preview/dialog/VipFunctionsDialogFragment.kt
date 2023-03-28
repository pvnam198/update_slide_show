package com.example.slide.ui.video.video_preview.dialog

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.photo_video_maker_with_song.network.NetworkHelper
import com.example.slide.R
import com.example.slide.ads.AdLoadingDialog
import com.example.slide.ads.OnRewardAdCallback
import com.example.slide.ads.RewardHelper
import com.example.slide.base.BaseDialogFragment
import com.example.slide.local.PreferencesHelper
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.ui.vip.FailedConnectDialogFragment
import com.example.slide.ui.vip.VipActivity
import com.example.slide.videolib.VideoConfig
import com.google.android.gms.ads.rewarded.RewardItem
import kotlinx.android.synthetic.main.dialog_vip_functions.*

class VipFunctionsDialogFragment : BaseDialogFragment(), View.OnClickListener, OnRewardAdCallback {

    override val layoutId: Int = R.layout.dialog_vip_functions

    private var adLoadingDialog: AdLoadingDialog? = null

    private var isVipFeature = false

    private var videoQuality = VideoConfig.VIDEO_QUALITY_480

    private var task = TASK_UNLOCK

    var isViewCreated = false

    companion object {

        const val TAG = "VipFunctionsDialogFragment"

        private const val ARG_VIP_FEATURE = "vip_feature"

        private const val ARG_RESOLUTION = "resolution"

        private const val ARG_TASK = "task"

        const val TASK_UNLOCK = 0

        const val TASK_SAVE = 1

        fun getInstance(
            isVipFeature: Boolean,
            resolution: Int,
            task: Int
        ): VipFunctionsDialogFragment {
            val vipFunctionsDialogFragment = VipFunctionsDialogFragment()
            vipFunctionsDialogFragment.arguments = Bundle().apply {
                putBoolean(ARG_VIP_FEATURE, isVipFeature)
                putInt(ARG_RESOLUTION, resolution)
                putInt(ARG_TASK, task)
            }
            return vipFunctionsDialogFragment
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ARG_VIP_FEATURE, isVipFeature)
        outState.putInt(ARG_RESOLUTION, videoQuality)
        outState.putInt(ARG_TASK, task)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
        RewardHelper.initFullScreenContentCallback()
        RewardHelper.onRewardAdCallback = this
        btn_cancel.setOnClickListener(this)
        btn_watch_ads.setOnClickListener(this)
        btn_become_vip.setOnClickListener(this)
        val bundle = savedInstanceState ?: requireArguments()

        isVipFeature = bundle.getBoolean(ARG_VIP_FEATURE)
        videoQuality = bundle.getInt(ARG_RESOLUTION, VideoConfig.VIDEO_QUALITY_480)
        task = bundle.getInt(ARG_TASK, TASK_UNLOCK)
        if (isVipFeature) {
            layout_vip_transitions.visibility = View.VISIBLE
        }
        if (videoQuality != VideoConfig.VIDEO_QUALITY_480) {
            showResolution(videoQuality)
        }
    }

    override fun onDestroyView() {
        RewardHelper.onRewardAdCallback = null
        isViewCreated = false
        super.onDestroyView()
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

    private fun showResolution(resolution: Int) {
        layout_resolution.visibility = View.VISIBLE
        when (resolution) {
            VideoConfig.VIDEO_QUALITY_1080 -> tv_resolution.text = getString(R.string.full_hd_1080p)
            VideoConfig.VIDEO_QUALITY_720 -> tv_resolution.text = getString(R.string.hd_720p)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("kimkakadialog", "onstart")
        val dialog = dialog
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
        if (PreferencesHelper(requireContext()).isVipOrVipTrialMember()) dismiss()
    }

    override fun onClick(view: View) {
        when (view) {
            btn_cancel -> {
                dismiss()
            }
            btn_watch_ads -> {
                loadRewardedAds()
            }
            btn_become_vip -> {
                startActivity(Intent(requireActivity(), VipActivity::class.java))
            }
        }
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
        PreferencesHelper(requireActivity()).setInterstitialDisplay()
    }

    override fun onAdDismissedFullScreenContent(isRewardItemFromUser: Boolean) {
        if (isRewardItemFromUser) {
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_thank_you),
                Toast.LENGTH_SHORT
            ).show()
            dismiss()
            if (task == TASK_UNLOCK) {
                (requireActivity() as VideoCreateActivity).requestToUseNormalFeature()
            } else {
                (requireActivity() as VideoCreateActivity).launchSaveVideoActivity(
                    videoQuality
                )
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_please_watch_ads),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onUserEarnedReward(rewardItem: RewardItem) {
        PreferencesHelper(requireContext()).enableVipTrail()
    }

    private fun dismissAdLoading() {
        adLoadingDialog?.dismiss()
        adLoadingDialog = null
    }

}
