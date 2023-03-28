package com.example.slide.ui.my_studio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.ui.select_image.SelectActivity
import com.example.slide.util.ShareUtils
import com.example.slide.util.StringUtils
import com.example.slide.util.Utils
import kotlinx.android.synthetic.main.item_video.view.*
import kotlinx.android.synthetic.main.item_video_create.view.*
import java.io.File

class VideoAdapter(
    private val onVideoClicked: (myVideo: MyVideo) -> Unit,
    private val onRenameClicked: (myVideo: MyVideo) -> Unit,
    private val onDeleteVideoClicked: (myVideo: MyVideo) -> Unit
) :
    RecyclerView.Adapter<VideoAdapter.VideoHolder>() {

    companion object {

        const val TYPE_ITEM = 0

        const val TYPE_CREATE = 1

        private const val TAG: String = "VideoAdapter"

    }

    private val videos = ArrayList<MyVideo>()

    fun updateData(nVideos: ArrayList<MyVideo>) {
        videos.clear()
        videos.addAll(nVideos)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (position == videos.size) return TYPE_CREATE
        return TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        val view = if (viewType == TYPE_ITEM) LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video, parent, false)
        else LayoutInflater.from(parent.context).inflate(R.layout.item_video_create, parent, false)
        return VideoHolder(view, viewType)
    }

    override fun getItemCount(): Int {
        if (videos.size > 0)
            return videos.size + 1
        return 0
    }

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        if (holder.viewType == TYPE_ITEM) {
            holder.btnVideo!!.setOnClickListener { onVideoClicked(videos[position]) }
            holder.btnMore!!.setOnClickListener {
                openMenu(it, videos[position])
            }
            Glide.with(holder.itemView).load(videos[position].url).into(holder.ivThumb!!)
            holder.tvDuration!!.text =
                StringUtils.getDurationDisplayFromMillis(videos[position].duration)
        } else {
            holder.btnCreateNewVideo!!.setOnClickListener {
                holder.itemView.context.startActivity(
                    SelectActivity.getIntent(
                        holder.itemView.context,
                        SelectActivity.MODE_START
                    )
                )
            }
        }
    }

    private fun openMenu(v: View, video: MyVideo) {
        val popup = PopupMenu(v.context, v)
        val view = if (Utils.isScopeStorage()) R.menu.video_menu_scope else R.menu.video_menu
        popup.menuInflater.inflate(view, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_rename -> {
                    onRenameClicked.invoke(video)
                }
                R.id.menu_share ->
                    ShareUtils.createShareMoreIntent(v.context, File(video.url))
                R.id.menu_delete -> {
                    onDeleteVideoClicked.invoke(video)
                }
            }
            true
        }
        popup.show()
    }

    class VideoHolder(itemView: View, val viewType: Int) : RecyclerView.ViewHolder(itemView) {
        val btnVideo: View? = itemView.btn_video
        val btnCreateNewVideo: View? = itemView.btn_create_new_video
        val btnMore: View? = itemView.btn_more
        val ivThumb: ImageView? = itemView.iv_thumb
        val tvDuration: TextView? = itemView.tv_duration
    }
}