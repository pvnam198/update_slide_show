package com.example.slide.ui.edit_image.fragment

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.ui.edit_image.EditImageActivity
import com.example.slide.ui.edit_image.adapter.ContentEmojiAdapter
import com.example.slide.ui.edit_image.adapter.EmojiAdapter
import com.example.slide.ui.edit_image.model.EmojiSticker
import com.example.slide.util.EmojiProvider
import kotlinx.android.synthetic.main.fragment_emojisticker_container.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StickerFragment : BaseFragment(), EmojiAdapter.IOnItemClicked,
        ContentEmojiAdapter.IOnItemClicked {

    override fun initViewTools() = InitViewTools({ R.layout.fragment_emojisticker_container })

    private lateinit var contentEmojiAdapter: ContentEmojiAdapter

    private val editImageActivity: EditImageActivity by lazy { requireActivity() as EditImageActivity }

    override fun initConfiguration() {
        super.initConfiguration()
        CoroutineScope(Dispatchers.IO).launch {
            val emojiStickers = EmojiProvider.getEmojiStickers(requireContext())
            CoroutineScope(Dispatchers.Main).launch {
                progress_bar.visibility = View.GONE
                representEmojiInit(emojiStickers)
                emojisInit(emojiStickers)
            }
        }
    }

    private fun emojisInit(emojiStickers: ArrayList<EmojiSticker>) {
        contentEmojiAdapter = ContentEmojiAdapter(editImageActivity, emojiStickers[0])
        emojis_contents.layoutManager =
                GridLayoutManager(requireContext(), 5, GridLayoutManager.VERTICAL, false)
        emojis_contents.adapter = contentEmojiAdapter
        contentEmojiAdapter.setIOnItemClicked(this)
    }

    private fun representEmojiInit(emojiStickers: ArrayList<EmojiSticker>) {
        val emojiAdapter = EmojiAdapter(emojiStickers)
        emojiAdapter.setIOnItemClicked(this)
        recycler_view_list_emoji.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recycler_view_list_emoji.adapter = emojiAdapter
    }

    override fun initTask() {
        super.initTask()
        iv_close.setOnClickListener {
            editImageActivity.removeSticker()
        }

        iv_check.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            editImageActivity.saveAndContinueEditImage()
        }
    }

    override fun releaseData() {}

    override fun onEmojiSelected(emojiSticker: EmojiSticker) {
        contentEmojiAdapter.setupData(emojiSticker)
    }

    override fun onEmojiSelected(emoji: String) {
        Glide.with(requireContext()).asBitmap().load(emoji)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                    ) {
                        editImageActivity.setEmoji(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
    }
}