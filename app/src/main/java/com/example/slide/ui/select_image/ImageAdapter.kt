package com.example.slide.ui.select_image

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.model.Image
import java.util.*

class ImageAdapter(
    val activity: SelectActivity
) :
    RecyclerView.Adapter<ImageAdapter.ImageHolder>() {

    private var images = ArrayList<Image>()

    fun updateData(
        images: ArrayList<Image>,
        selectedImages: ArrayList<Image>
    ) {
        this.images = images
        images.forEach { image ->
            image.resetCount()
            selectedImages.forEach {
                if (image.id == it.id) image.increaseSelectCount()
            }
        }
        notifyDataSetChanged()
    }

    fun updateSelectedData(selectedImages: ArrayList<Image>) {
        images.forEach { image ->
            image.resetCount()
            selectedImages.forEach {
                if (image.id == it.id) image.increaseSelectCount()
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_image_layout, parent, false)
        return ImageHolder(view)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        val image = images[position]
        Glide.with(activity).load(image.url).into(holder.image)
        if (image.isSelected()) {
            holder.view_color.visibility = View.VISIBLE
            holder.tv_count.visibility = View.VISIBLE
            holder.tv_count.text = String.format(Locale.US, "%02d", image.countNumber)
        } else {
            holder.tv_count.visibility = View.GONE
            holder.view_color.visibility = View.GONE
        }
        holder.view_image.setOnClickListener {
            activity.setSelectedImages(image, position)
        }

    }

    class ImageHolder(view: View) : RecyclerView.ViewHolder(view) {

        val image = view.findViewById<ImageView>(R.id.btn_icon)

        val tv_count = view.findViewById<TextView>(R.id.tv_count)

        val view_image = view.findViewById<View>(R.id.view_image)

        val view_color = view.findViewById<View>(R.id.view_color)
    }
}