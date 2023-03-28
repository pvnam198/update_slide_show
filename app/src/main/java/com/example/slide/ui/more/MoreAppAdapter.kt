package com.example.slide.ui.more

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.framework.thirdparty.NavApp
import com.example.slide.framework.thirdparty.ThirdPartyRequest
import com.example.slide.util.isPackageInstalled
import kotlinx.android.synthetic.main.item_app.view.*

class MoreAppAdapter(private val onInstallClicked: (app: NavApp) -> Unit) :
    RecyclerView.Adapter<MoreAppAdapter.ViewHolder>() {

    private var navApps: List<NavApp> = ThirdPartyRequest.apps

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.iv_icon
        val tvTitle: TextView = view.tv_title
        val btnInstall: View = view.btn_install
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_app,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = navApps[position]

        if (holder.itemView.context.isPackageInstalled(app.vPackage)) {
            holder.itemView.visibility = View.GONE
        } else {
            holder.itemView.visibility = View.VISIBLE
            Glide.with(holder.itemView).load(app.icon).into(holder.ivIcon)
            holder.tvTitle.text = app.name
            holder.btnInstall.setOnClickListener { onInstallClicked(app) }
        }
    }

    override fun getItemCount(): Int = navApps.size
}