package com.example.slide.ui.edit_image.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.util.ColorProvider

class ColorTextAdapter(
    private val onColorSelected: (Int, Int) -> Unit
) :
    RecyclerView.Adapter<ColorTextAdapter.ViewHolder>() {

    private val colorList: ArrayList<Int> = arrayListOf()

    private var currentPosition: Int = 0

    init {
        ColorProvider.colors().forEach { color ->
            colorList.add(Color.parseColor(color))
        }
    }

    fun updateSelectedColor(selectedColor: Int) {
        this.currentPosition = selectedColor
        notifyDataSetChanged()
    }

    fun disableSelected() {
        currentPosition = -1
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
        holder.colorPickerView.setBackgroundColor(color)

        holder.colorPickerView.setOnClickListener {
            currentPosition = position
            notifyDataSetChanged()
            onColorSelected.invoke(color, position)
        }

        holder.border.isSelected = currentPosition == position
    }

    override fun getItemCount() = colorList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val colorPickerView: View = view.findViewById(R.id.color_picker_view)
        val border: View = view.findViewById(R.id.border)
    }

}