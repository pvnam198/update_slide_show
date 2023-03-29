package com.example.slide.ui.select_image

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.model.Image

class ImageSelectedAdapter(
    val images: ArrayList<Image>,
    private val context: Context,
    private val fragment: SelectFragment
) :
    RecyclerView.Adapter<ImageSelectedAdapter.ImageSelectedHolder>() {

    fun removeAllData() {
        images.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSelectedHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_selected_layout, parent, false)
        return ImageSelectedHolder(view)
    }

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: ImageSelectedHolder, position: Int) {
        Glide.with(context).load(images[position].url).into(holder.image)
        holder.btn_remove_image.setOnClickListener {
            fragment.removeSelectedImageAt(position)
        }

    }

    class ImageSelectedHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.btn_icon)
        val btn_remove_image = view.findViewById<View>(R.id.btn_remove_image)
    }
}