package com.example.slide.ui.edit_image.adapter

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
import kotlinx.android.synthetic.main.item_emoji.view.*


class EmojiAdapter(private var emojiStickers: ArrayList<EmojiSticker>) :
    RecyclerView.Adapter<EmojiAdapter.ViewHolder>() {

    private var rowIndex: Int = 0

    init {
        setHasStableIds(true)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivEmoji: AppCompatImageView = view.iv_emoji
        val ivVip: AppCompatImageView = view.iv_vip
        val layoutEmoji: ConstraintLayout = view.layout_emoji
        val btnEmoji: View = view.btn_emoji
    }

    private lateinit var iOnItemClicked: IOnItemClicked
    fun setIOnItemClicked(iOnItemClicked: IOnItemClicked) {
        this.iOnItemClicked = iOnItemClicked
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_emoji, parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stickerOb = emojiStickers[position]
        if (stickerOb.isVip)
            holder.ivVip.visibility = View.VISIBLE
        else
            holder.ivVip.visibility = View.GONE

        val uri = Uri.parse(stickerOb.emojiPath + stickerOb.represent)
        Glide.with(holder.ivEmoji).load(uri).into(holder.ivEmoji)

        holder.btnEmoji.setOnClickListener {
            if (rowIndex == position) return@setOnClickListener
            iOnItemClicked.onEmojiSelected(stickerOb)
            rowIndex = position
            notifyDataSetChanged()
        }
        holder.layoutEmoji.isSelected = rowIndex == position
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = emojiStickers.size

    interface IOnItemClicked {
        fun onEmojiSelected(emojiSticker: EmojiSticker)
    }
}