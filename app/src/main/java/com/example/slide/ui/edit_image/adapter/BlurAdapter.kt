package com.example.slide.ui.edit_image.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.ui.edit_image.BlurProvider
import kotlinx.android.synthetic.main.overlay_item_layout.view.*

class BlurAdapter(
    private val bitmap: Bitmap,
    val context: Context,
    private val splashItemOnclickCallback: ((BlurProvider.BlurMask?)) -> Unit
) : RecyclerView.Adapter<BlurAdapter.ViewHolder>() {

    private var currentIndex = 0

    private val items = BlurProvider.BLURS

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.overlay_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size + 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (position == 0) {
            holder.ivIcon.setImageBitmap(bitmap)
        } else {
            val item = items[position - 1]
            holder.ivIcon.setImageResource(item.imageRes)
        }
        holder.ivIcon.setOnClickListener {
            if (currentIndex != position) {
                currentIndex = position
                notifyDataSetChanged()
                splashItemOnclickCallback.invoke(if (position == 0) null else items[position - 1])
            }
        }
        holder.layoutSelected.visibility = if (position == currentIndex) View.VISIBLE else View.GONE
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: AppCompatImageView = view.btn_icon
        val layoutSelected: View = view.layout_selected
        val rootView: View = view.root_view
    }

}