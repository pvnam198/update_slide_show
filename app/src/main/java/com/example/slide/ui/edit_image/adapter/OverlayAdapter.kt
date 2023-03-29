package com.example.slide.ui.edit_image.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.ui.edit_image.utils.FilterUtils

class OverlayAdapter(
    private var bitmaps: ArrayList<Bitmap>,
    private var filterBeans: ArrayList<FilterUtils.FilterBean>,
    private val overlayCallback: (String, Int) -> Unit
) : RecyclerView.Adapter<OverlayAdapter.ViewHolder>() {

    private var currentIndex = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.overlay_item_layout,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount() = bitmaps.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.ivIcon.setImageBitmap(bitmaps[position])

        holder.btnIcon.setOnClickListener {
            currentIndex = position
            overlayCallback.invoke(filterBeans[position].config, position)
            notifyDataSetChanged()
        }

        holder.layoutSelected.visibility = if (currentIndex == position) View.VISIBLE else View.INVISIBLE

    }

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val ivIcon: AppCompatImageView = view.findViewById(R.id.btn_icon)
        val btnIcon: View = view.findViewById(R.id.btn_icon)
        val layoutSelected: View = view.findViewById(R.id.layout_selected)
    }
}