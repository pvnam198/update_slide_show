package com.example.slide.ui.select_image

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.slide.R
import com.example.slide.model.Image
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.item_swaps_layout.view.*

class SwapsAdapter(
    val activity: SelectActivity,
    private val images: ArrayList<Image>
) : RecyclerView.Adapter<SwapsAdapter.SwapsViewHolder>(),
    DraggableItemAdapter<SwapsAdapter.SwapsViewHolder> {

    private var positionImageEdit = -1

    private lateinit var viewOverLay: FrameLayout

    init {
        setHasStableIds(true)
    }

    class SwapsViewHolder(view: View) : AbstractDraggableItemViewHolder(view) {
        val buttonStt: Button = view.button_stt
        val ivSwap: ImageView = view.iv_swap
        val btnEdit: View = view.btn_edit
        val btnZoom: View = view.btn_zoom
        val ivImageDisplay: ImageView = view.iv_image_display
        val imageBlur: AppCompatImageView = view.image_blur
        val btnRemove: View = view.btn_remove
        val itemRootView: FrameLayout = view.item_root_view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwapsViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_swaps_layout, parent, false)
        return SwapsViewHolder(
            view
        )
    }

    override fun getItemId(position: Int): Long {
        return images[position].uniqueId.toLong()
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: SwapsViewHolder, position: Int) {
        val image = images[position]
        Glide.with(activity).load(image.url)
            .apply(RequestOptions.overrideOf(200, 200))
            .into(holder.ivImageDisplay)
        Glide.with(activity).load(image.url)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
            .into(holder.imageBlur)
        holder.buttonStt.text = (position+1).toString()
        holder.btnZoom.setOnClickListener {
            activity.zoomImage(image.url)
        }
        holder.btnEdit.setOnClickListener {
            positionImageEdit = position
            activity.goToEditImage(image.url, position)
        }

        holder.btnRemove.setOnClickListener {
            if (images.size > 2) {
                activity.removeImageFromSwapScreen(image)
            } else {
                activity.showMissingImage()
            }
        }

        // set background resource (target view ID: container)
        val dragState = holder.dragState
        if (dragState.isUpdated) {
            if (dragState.isActive) {
                holder.itemRootView.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.colorItemSelected
                    )
                )
            } else {
                holder.itemRootView.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.colorBG
                    )
                )
            }
        }
    }

    override fun onGetItemDraggableRange(
        holder: SwapsViewHolder,
        position: Int
    ): ItemDraggableRange? {
        return null
    }

    override fun onCheckCanStartDrag(
        holder: SwapsViewHolder,
        position: Int,
        x: Int,
        y: Int
    ): Boolean {
        viewOverLay = holder.itemRootView
        val dragHandleView: View = holder.ivSwap
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
        val item = activity.selectedImages.removeAt(fromPosition)
        activity.selectedImages.add(toPosition, item)
        activity.editImageDone()
    }

    override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean {
        return true
    }

    override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) {
        activity.lifecycleScope.launchWhenResumed { activity.saveDraft() }
        notifyDataSetChanged()
    }
}