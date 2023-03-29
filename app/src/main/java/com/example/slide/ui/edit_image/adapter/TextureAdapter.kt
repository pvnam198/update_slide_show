package com.example.slide.ui.edit_image.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R

class TextureAdapter(
    private val onColorSelected: (Bitmap, Int) -> Unit
) :
    RecyclerView.Adapter<TextureAdapter.ViewHolder>() {

    private val colorList: ArrayList<Bitmap> = ArrayList()

    private var selectedColor: Int = -1

    fun updateData(bitmaps: ArrayList<Bitmap>){
        colorList.clear()
        colorList.addAll(bitmaps)
        notifyDataSetChanged()
    }

    fun updateSelectedColor(selectedColor: Int) {
        this.selectedColor = selectedColor
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_recyler_color,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val color = colorList[position]

        holder.colorPickerView.setImageBitmap(color)

        holder.colorPickerView.setOnClickListener {
            selectedColor = position
            notifyDataSetChanged()
            onColorSelected.invoke(color, position)
        }

        holder.border.isSelected = selectedColor == position
    }

    override fun getItemCount() = colorList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val colorPickerView: AppCompatImageView = view.findViewById(R.id.color_picker_view)
        val border: View = view.findViewById(R.id.border)
    }

}