package com.example.slide.ui.video.video_preview.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.local.PreferencesHelper
import com.example.slide.ui.edit_image.model.EmojiSticker
import kotlinx.android.synthetic.main.item_emoji.view.iv_emoji
import kotlinx.android.synthetic.main.item_emoji_content.view.*

class VideoStickerContentAdapter(
    private val context: Context,
    var emojiSticker: EmojiSticker,
    private val onEmojiSelected: (String) -> Unit,
    private val showVIPDialog: () -> Unit
) :
    RecyclerView.Adapter<VideoStickerContentAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "VideoStickerContentAdap"
    }


    fun setupData(emojiSticker: EmojiSticker) {
        this.emojiSticker = emojiSticker
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iv_emoji = view.iv_emoji
        val btn_emoji = view.btn_emoji
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_emoji_content, parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount() = emojiSticker.emojis.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val emoji = emojiSticker.emojiPath + emojiSticker.emojis[position]
        Glide.with(context)
            .load(Uri.parse(emoji))
            .into(holder.iv_emoji)

        holder.btn_emoji.setOnClickListener {
            if (emojiSticker.isVip) {
                if (!PreferencesHelper(context).isVipOrVipTrialMember()) {
                    showVIPDialog.invoke()
                } else {
                    onEmojiSelected.invoke(emoji)
                }
            } else {
                onEmojiSelected.invoke(emoji)
            }
        }
    }
}