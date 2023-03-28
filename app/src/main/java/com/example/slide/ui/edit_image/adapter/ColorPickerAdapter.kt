package com.example.slide.ui.edit_image.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.util.ColorProvider
import kotlinx.android.synthetic.main.item_recyler_color.view.*

class ColorPickerAdapter(
    private val onColorSelected: (Int) -> Unit
) :
    RecyclerView.Adapter<ColorPickerAdapter.ViewHolder>() {

    private val colorList: ArrayList<Int> = arrayListOf()

    private var selectedColor: Int = 0

    init {
        ColorProvider.colors().forEach { color ->
            colorList.add(Color.parseColor(color))
        }
        selectedColor = colorList[0]
    }

    fun updateSelectedColor(selectedColor: Int) {
        this.selectedColor = selectedColor
        notifyDataSetChanged()
    }

    fun disableSelected() {
        selectedColor = -10
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
            selectedColor = color
            notifyDataSetChanged()
            onColorSelected.invoke(color)
        }

        holder.border.isSelected = selectedColor == color
    }

    override fun getItemCount() = colorList.size

    fun getSelectedPos(): Int {
        colorList.forEachIndexed { index, color ->
            if (color == selectedColor) return index
        }
        return 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val colorPickerView: View = view.color_picker_view
        val border: View = view.border
    }

}