package com.example.slide.ui.video.video_preview.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.ui.edit_image.model.EmojiSticker

class VideoStickerAdapter(
    private var emojiStickers: ArrayList<EmojiSticker>,
    private val onEmojiSelected: (EmojiSticker) -> Unit
) :
    RecyclerView.Adapter<VideoStickerAdapter.ViewHolder>() {

    private var rowIndex: Int = 0

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivEmoji: AppCompatImageView = view.findViewById(R.id.iv_emoji)
        val ivVip: AppCompatImageView = view.findViewById(R.id.iv_vip)
        val layoutEmoji: ConstraintLayout = view.findViewById(R.id.layout_emoji)
        val btnEmoji: View = view.findViewById(R.id.btn_emoji)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_emoji, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val stickerOb = emojiStickers[position]
        if (stickerOb.isVip)
            holder.ivVip.visibility = View.VISIBLE
        else
            holder.ivVip.visibility = View.GONE

        Glide.with(holder.ivEmoji)
            .load(Uri.parse(stickerOb.emojiPath + stickerOb.represent))
            .into(holder.ivEmoji)

        holder.btnEmoji.setOnClickListener {
            onEmojiSelected.invoke(stickerOb)
            rowIndex = position
            notifyDataSetChanged()
        }
        holder.layoutEmoji.isSelected = rowIndex == position
    }

    override fun getItemCount() = emojiStickers.size

}