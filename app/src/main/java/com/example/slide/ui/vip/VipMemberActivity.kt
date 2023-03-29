package com.example.slide.ui.vip

import com.example.slide.R
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.ActivityVipMemberBinding

class VipMemberActivity : BaseActivity<ActivityVipMemberBinding>() {
    override fun bindingView(): ActivityVipMemberBinding {
        return ActivityVipMemberBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({
        R.layout.activity_vip_member
    })

    override fun releaseData() {
    }

    override fun initListener() {
        super.initListener()
        binding.btnCancel.setOnClickListener {
            onBackPressed()
        }
    }
}