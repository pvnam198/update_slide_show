package com.example.slide.ui.video.video_preview.fragments

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import com.example.slide.databinding.FragmentStickerVideoBinding
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.ui.video.video_preview.adapter.VideoStickerAdapter
import com.example.slide.ui.video.video_preview.adapter.VideoStickerContentAdapter
import com.example.slide.framework.texttovideo.DrawableVideoFloatingItem
import com.example.slide.ui.edit_image.VipEmojiDialogFragment
import com.example.slide.util.EmojiProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmojiVideoFragment : BaseFragment<FragmentStickerVideoBinding>(), View.OnClickListener {

    private lateinit var videoStickerAdapter: VideoStickerAdapter

    private lateinit var videoStickerContentAdapter: VideoStickerContentAdapter

    private var duration = 0

    private var startTime = 0

    private var endTime = 0

    private var customTarget : CustomTarget<Bitmap>? = null

    companion object {
        const val TAG = "VideoStickerFragment"
    }

    override fun bindingView(): FragmentStickerVideoBinding {
        return FragmentStickerVideoBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({
        R.layout.fragment_sticker_video
    })

    override fun initConfiguration() {
        super.initConfiguration()

        duration =
            (requireActivity() as VideoCreateActivity).myApplication.videoDataState.totalSecond
        endTime = duration

        CoroutineScope(Dispatchers.IO).launch {
            val emojiStickers = EmojiProvider.getEmojiStickers(requireContext())
            CoroutineScope(Dispatchers.Main).launch {

                if (isBind){
                    videoStickerAdapter = VideoStickerAdapter(emojiStickers, onEmojiSelected = {
                        videoStickerContentAdapter.setupData(it)
                    })
                    binding.recyclerViewListEmoji.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.recyclerViewListEmoji.adapter = videoStickerAdapter

                    videoStickerContentAdapter =
                        VideoStickerContentAdapter(
                            requireContext(),
                            emojiStickers[0],
                            onEmojiSelected = { iconPath ->
                                customTarget = Glide.with(requireContext()).asBitmap().load(iconPath)
                                    .into(object : CustomTarget<Bitmap>() {
                                        override fun onResourceReady(
                                            resource: Bitmap,
                                            transition: Transition<in Bitmap>?
                                        ) {
                                            val drawableVideoSticker =
                                                DrawableVideoFloatingItem(
                                                    BitmapDrawable(
                                                        requireContext().resources, resource
                                                    ), iconPath, startTime, endTime
                                                )
                                            (activity as VideoCreateActivity).setEmoji(
                                                drawableVideoSticker
                                            )
                                            customTarget = null
                                        }

                                        override fun onLoadCleared(placeholder: Drawable?) {
                                        }
                                    })
                            }, showVIPDialog = {
                                VipEmojiDialogFragment.createInstance()
                                    .show(parentFragmentManager, VipEmojiDialogFragment.TAG)
                            })
                    binding.emojisContents.layoutManager =
                        GridLayoutManager(requireContext(), 5, GridLayoutManager.VERTICAL, false)
                    binding.emojisContents.adapter = videoStickerContentAdapter

                    binding.progressBar.visibility = View.INVISIBLE
                }

            }
        }
    }

    override fun initListener() {
        super.initListener()
        binding.ivCheck.setOnClickListener(this)
        binding.ivClose.setOnClickListener(this)
    }

    override fun releaseData() {
        customTarget?.let {
            Glide.with(this).clear(it)
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.ivCheck, binding.ivClose ->
                (requireActivity() as VideoCreateActivity).onBackPressed()
        }
    }
}