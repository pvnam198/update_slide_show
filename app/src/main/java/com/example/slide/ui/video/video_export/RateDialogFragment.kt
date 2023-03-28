package com.example.slide.ui.video.video_export

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.ads.Ads
import com.example.slide.local.PreferencesHelper
import com.example.slide.util.rateApp
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.dialog_rate_app_v2.*

class RateDialogFragment : DialogFragment(), View.OnClickListener {

    companion object {

        private fun getInstance(preferencesHelper: PreferencesHelper): RateDialogFragment {
            return RateDialogFragment().apply {
                this.preferencesHelper = preferencesHelper
            }
        }

        fun show(appCompatActivity: AppCompatActivity, preferencesHelper: PreferencesHelper) {
            if (preferencesHelper.isAppRated() || preferencesHelper.isNeverRateApp())
                return
            val isTime = if (!preferencesHelper.isFirstRate()) {
                Ads.firstRate
            } else {
                Ads.laterRate
            }
            if (preferencesHelper.isExportShowRate(isTime)) {
                if (!preferencesHelper.isFirstRate()) {
                    preferencesHelper.setFirstRate()
                }
                getInstance(preferencesHelper).show(
                    appCompatActivity.supportFragmentManager,
                    "rate_app"
                )
            }
        }

    }

    private var preferencesHelper: PreferencesHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_rate_app_v2, container)
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

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
        Glide.with(requireContext())
            .load(Uri.parse("file:///android_asset/dialog_vote.gif"))
            .into(iv_rate)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_later.setOnClickListener(this)
        btn_rate.setOnClickListener(this)
        btn_never.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view) {
            btn_later -> {
                FirebaseAnalytics.getInstance(requireContext()).logEvent("rate_later", null)
                preferencesHelper?.rateLater()
                dismiss()
            }
            btn_rate -> {
                FirebaseAnalytics.getInstance(requireContext()).logEvent("rate", null)
                preferencesHelper?.setAppRated()
                requireContext().rateApp(requireActivity().packageName)
                dismiss()
            }
            btn_never -> {
                FirebaseAnalytics.getInstance(requireContext()).logEvent("rate_never", null)
                preferencesHelper?.neverRateApp()
                dismiss()
            }
        }
    }

}