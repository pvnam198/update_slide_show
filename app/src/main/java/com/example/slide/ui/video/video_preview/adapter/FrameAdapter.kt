package com.example.slide.ui.video.video_preview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.ui.video.video_preview.FrameProvider
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.ui.video.video_preview.model.VideoFrame
import kotlinx.android.synthetic.main.item_frame_view.view.*

class FrameAdapter(private val activity: VideoCreateActivity) :
        RecyclerView.Adapter<FrameAdapter.FrameHolder>() {

    private val videoFrames: Array<VideoFrame> = FrameProvider.FRAMES

    private var currentPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrameHolder {
        val frameView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_frame_view, parent, false)
        return FrameHolder(frameView)
    }

    override fun getItemCount() = videoFrames.size

    override fun onBindViewHolder(holder: FrameHolder, position: Int) {
        val frame = videoFrames[position]
        Glide.with(activity).load(frame.getUri()).into(holder.frameImage)
        holder.frameImage.isSelected = currentPosition == position
        holder.itemView.setOnClickListener {
            if (currentPosition != position) {
                currentPosition = position
                activity.setFrameForImagePreview(frame, position)
                notifyDataSetChanged()
            }
        }
    }

    fun reinstallFrame(position: Int) {
        activity.setFrameForImagePreview(videoFrames[position], position)
        currentPosition = position
        notifyDataSetChanged()
    }

    fun reinstallFrame(videoFrame: VideoFrame?) {
        videoFrame?.let {
            currentPosition = videoFrames.indexOf(it)
            reinstallFrame(currentPosition)
        }
    }

    class FrameHolder(view: View) : RecyclerView.ViewHolder(view) {
        val frameImage: AppCompatImageView = view.iv_item_image
    }
}