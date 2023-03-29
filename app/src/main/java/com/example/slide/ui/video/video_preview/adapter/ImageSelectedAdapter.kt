package com.example.slide.ui.video.video_preview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.model.Image
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder
import java.util.*

class ImageSelectedAdapter(val activity: VideoCreateActivity) :
        RecyclerView.Adapter<ImageSelectedAdapter.ViewHolder>(),
        DraggableItemAdapter<ImageSelectedAdapter.ViewHolder> {

    var images: ArrayList<Image>

    init {
        images = activity.dataPreview.images
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return images[position].id.toLong()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_selected, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = images[position]
        Glide.with(activity).load(images[position].url).into(holder.image_view)
        holder.image_unselected.setOnClickListener {
            if (images.size > 2) {
                images.remove(image)
                notifyDataSetChanged()
                activity.restartPlayingService()
            } else {
                Toast.makeText(
                        activity,
                        activity.getString(R.string.requite_images),
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onGetItemDraggableRange(holder: ViewHolder, position: Int): ItemDraggableRange? {
        return null
    }

    override fun onCheckCanStartDrag(holder: ViewHolder, position: Int, x: Int, y: Int): Boolean {
        val dragHandleView = holder.iv_swap
        val handleWidth = dragHandleView.width
        val handleHeight = dragHandleView.height
        val handleLeft = dragHandleView.left
        val handleTop = dragHandleView.top
        return (x >= handleLeft) && (x < handleLeft + handleWidth) &&
                (y >= handleTop) && (y < handleTop + handleHeight)
    }

    override fun onItemDragStarted(position: Int) {
        notifyDataSetChanged()
    }

    override fun onMoveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(images, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(images, i, i - 1)
            }
        }
    }

    override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean {
        return true
    }

    override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) {
        notifyDataSetChanged()
        activity.restartPlayingService()
    }

    fun updateImages() {
        images = activity.dataPreview.images
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : AbstractDraggableItemViewHolder(view) {

        val image_view = view.findViewById<ImageView>(R.id.btn_icon)

        val image_unselected = view.findViewById<AppCompatImageView>(R.id.image_unselected)

        val iv_swap = view.findViewById<AppCompatImageView>(R.id.iv_swap)
    }
}