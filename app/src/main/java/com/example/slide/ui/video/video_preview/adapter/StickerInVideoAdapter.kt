package com.example.slide.ui.video.video_preview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.framework.texttovideo.DrawableVideoFloatingItem
import com.example.slide.util.StringUtils

class StickerInVideoAdapter(
    private val removeItem: (DrawableVideoFloatingItem) -> Unit,
    private val editItem: (DrawableVideoFloatingItem) -> Unit,
) :
    RecyclerView.Adapter<StickerInVideoAdapter.ViewHolder>() {

    private val drawableVideoStickers = ArrayList<DrawableVideoFloatingItem>()

    fun addSticker(drawableVideoSticker: DrawableVideoFloatingItem) {
        drawableVideoStickers.add(drawableVideoSticker)
        notifyDataSetChanged()
    }

    fun updateData(drawableVideoStickers: ArrayList<DrawableVideoFloatingItem>) {
        this.drawableVideoStickers.clear()
        this.drawableVideoStickers.addAll(drawableVideoStickers)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivEmoji: AppCompatImageView = view.findViewById(R.id.iv_emoji)
        val tvEndTime: AppCompatTextView = view.findViewById(R.id.tv_end_time)
        val tvStartTime: AppCompatTextView = view.findViewById(R.id.tv_start_time)
        val btnEdit: View = view.findViewById(R.id.btn_edit)
        val btnRemove: View = view.findViewById(R.id.btn_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_sticker_in_video, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val drawableVideoSticker = drawableVideoStickers[position]
        Glide.with(holder.itemView.context).load(drawableVideoSticker.iconPath).into(holder.ivEmoji)
        holder.tvStartTime.text =
            StringUtils.getDurationDisplayFromSeconds(drawableVideoSticker.startTime)
        holder.tvEndTime.text =
            StringUtils.getDurationDisplayFromSeconds(drawableVideoSticker.endTime)
        holder.btnRemove.setOnClickListener { removeItem.invoke(drawableVideoSticker) }
        holder.btnEdit.setOnClickListener { editItem.invoke(drawableVideoSticker) }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = drawableVideoStickers.size

}