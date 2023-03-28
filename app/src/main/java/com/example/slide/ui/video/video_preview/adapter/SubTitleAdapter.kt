package com.example.slide.ui.video.video_preview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.framework.texttovideo.VideoTextFloatingItem
import com.example.slide.util.StringUtils
import kotlinx.android.synthetic.main.item_sub_title.view.*

class SubTitleAdapter(
    private var totalTime: Int,
    newSubtitles: ArrayList<VideoTextFloatingItem>,
    private val removeListener: (textSticker: VideoTextFloatingItem) -> Unit,
    private val editListener: (textSticker: VideoTextFloatingItem) -> Unit
) :
    RecyclerView.Adapter<SubTitleAdapter.ViewHolder>() {

    private val subTitles = ArrayList<VideoTextFloatingItem>()

    init {
        subTitles.addAll(newSubtitles)
    }

    fun updateData(newSubtitles: ArrayList<VideoTextFloatingItem>) {
        subTitles.clear()
        subTitles.addAll(newSubtitles)
        notifyDataSetChanged()
    }

    fun changeTotalTime(totalTime: Int) {
        this.totalTime = totalTime
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_sub_title, parent, false)
        return ViewHolder(view, removeListener, editListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subTitles[position],totalTime)
        //
    }

    override fun getItemCount() = subTitles.size

    class ViewHolder(
        view: View,
        private val removeListener: (textSticker: VideoTextFloatingItem) -> Unit,
        private val editListener: (textSticker: VideoTextFloatingItem) -> Unit
    ) :
        RecyclerView.ViewHolder(view) {

        private val tvSubTitleName: AppCompatTextView = view.tv_sub_title_name
        private val tvStartTime: AppCompatTextView = view.tv_start_time
        private val tvEndTime: AppCompatTextView = view.tv_end_time
        private val btnRemove = view.btn_remove
        private val btnEdit = view.btn_edit

        private lateinit var currentVideoTextSticker: VideoTextFloatingItem

        fun bind(videoTextSticker: VideoTextFloatingItem, totalTime: Int) {
            currentVideoTextSticker = videoTextSticker
            tvSubTitleName.text = videoTextSticker.addTextProperties.text
            tvStartTime.text = StringUtils.getDurationDisplayFromSeconds(videoTextSticker.startTime)
            if(videoTextSticker.isFullTime) {
                tvEndTime.text = StringUtils.getDurationDisplayFromSeconds(totalTime)
            } else {
                tvEndTime.text = StringUtils.getDurationDisplayFromSeconds(videoTextSticker.endTime)
            }
        }

        init {
            btnRemove.setOnClickListener {
                removeListener.invoke(currentVideoTextSticker)
            }
            btnEdit.setOnClickListener {
                editListener.invoke(currentVideoTextSticker)
            }
        }
    }
}