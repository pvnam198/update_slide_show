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
import com.example.slide.databinding.FragmentEmojistickerContainerBinding
import com.example.slide.ui.edit_image.EditImageActivity
import com.example.slide.ui.edit_image.adapter.ContentEmojiAdapter
import com.example.slide.ui.edit_image.adapter.EmojiAdapter
import com.example.slide.ui.edit_image.model.EmojiSticker
import com.example.slide.util.EmojiProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StickerFragment : BaseFragment<FragmentEmojistickerContainerBinding>(), EmojiAdapter.IOnItemClicked,
        ContentEmojiAdapter.IOnItemClicked {
    override fun bindingView(): FragmentEmojistickerContainerBinding {
        return FragmentEmojistickerContainerBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({ R.layout.fragment_emojisticker_container })

    private lateinit var contentEmojiAdapter: ContentEmojiAdapter

    private val editImageActivity: EditImageActivity by lazy { requireActivity() as EditImageActivity }

    override fun initConfiguration() {
        super.initConfiguration()
        CoroutineScope(Dispatchers.IO).launch {
            val emojiStickers = EmojiProvider.getEmojiStickers(requireContext())
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar.visibility = View.GONE
                representEmojiInit(emojiStickers)
                emojisInit(emojiStickers)
            }
        }
    }

    private fun emojisInit(emojiStickers: ArrayList<EmojiSticker>) {
        contentEmojiAdapter = ContentEmojiAdapter(editImageActivity, emojiStickers[0])
        binding.emojisContents.layoutManager =
                GridLayoutManager(requireContext(), 5, GridLayoutManager.VERTICAL, false)
        binding.emojisContents.adapter = contentEmojiAdapter
        contentEmojiAdapter.setIOnItemClicked(this)
    }

    private fun representEmojiInit(emojiStickers: ArrayList<EmojiSticker>) {
        val emojiAdapter = EmojiAdapter(emojiStickers)
        emojiAdapter.setIOnItemClicked(this)
        binding.recyclerViewListEmoji.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewListEmoji.adapter = emojiAdapter
    }

    override fun initTask() {
        super.initTask()
        binding.ivClose.setOnClickListener {
            editImageActivity.removeSticker()
        }

        binding.ivCheck.setOnClickListener {
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