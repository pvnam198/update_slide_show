package com.example.slide.ui.vip

import com.example.slide.R
import com.example.slide.base.BaseActivity
import com.example.slide.base.InitViewTools
import kotlinx.android.synthetic.main.activity_vip_member.*

class VipMemberActivity : BaseActivity() {

    override fun initViewTools() = InitViewTools({
        R.layout.activity_vip_member
    })

    override fun releaseData() {
    }

    override fun initListener() {
        super.initListener()
        btn_cancel.setOnClickListener {
            onBackPressed()
        }
    }
}