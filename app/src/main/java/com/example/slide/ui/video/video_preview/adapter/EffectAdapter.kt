package com.example.slide.ui.video.video_preview.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.ui.video.video_preview.ThemeProvider
import com.example.slide.ui.video.video_preview.VideoCreateActivity

class EffectAdapter(val activity: VideoCreateActivity) :
    RecyclerView.Adapter<EffectAdapter.ViewHolder>() {

    private val themes = ThemeProvider.THEMES

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_video_theme, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = themes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val theme = themes[position]
        holder.imageLogo.setImageResource(theme.image)

        if (position == 0) {
            holder.tvNone.visibility = View.VISIBLE
            holder.layoutContent.visibility = View.GONE
        } else {
            holder.tvNone.visibility = View.GONE
            holder.layoutContent.visibility = View.VISIBLE
        }

        if (theme.style == ThemeProvider.Theme.STYLE_3D) {
            holder.ivEffectTag3d.visibility = View.VISIBLE
        } else {
            holder.ivEffectTag3d.visibility = View.GONE
        }

        if (theme.style == ThemeProvider.Theme.STYLE_PRESET) {
            holder.ivEffectTagRandom.visibility = View.VISIBLE
        } else {
            holder.ivEffectTagRandom.visibility = View.GONE
        }

        if (theme.isPremium) {
            holder.ivEffectVip.visibility = View.VISIBLE
        } else {
            holder.ivEffectVip.visibility = View.GONE
        }

        holder.tvEffectName.setText(theme.nameRes)
        if (theme.id == activity.dataPreview.selectedTheme.id) {

            if(position == 0){
                holder.tvNone.isSelected = true
            }

            holder.viewSelected.visibility = View.VISIBLE
            holder.tvEffectName.setTextColor(
                ContextCompat.getColor(
                    activity,
                    R.color.colorBlue
                )
            )
        } else {
            if(position == 0){
                holder.tvNone.isSelected = false
            }
            holder.viewSelected.visibility = View.GONE
            holder.tvEffectName.setTextColor(Color.WHITE)
        }
        holder.itemView.setOnClickListener {
            if (activity.applyTheme(theme)) notifyDataSetChanged()
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val btnTheme: View = view.findViewById(R.id.btn_theme)

        val viewSelected: View = view.findViewById(R.id.view_selected)

        val imageLogo: AppCompatImageView = view.findViewById(R.id.image_logo)

        val tvEffectName: AppCompatTextView = view.findViewById(R.id.tv_effect_name)

        val ivEffectTag3d: AppCompatImageView = view.findViewById(R.id.iv_effect_tag_3d)

        val ivEffectTagRandom: AppCompatImageView = view.findViewById(R.id.iv_effect_tag_random)

        val ivEffectVip: AppCompatImageView = view.findViewById(R.id.iv_effect_vip)

        val tvNone: AppCompatTextView = view.findViewById(R.id.tvNone)

        val layoutContent: ConstraintLayout = view.findViewById(R.id.layoutContent)
    }
}