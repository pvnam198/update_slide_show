package com.example.slide.ui.edit_image.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.util.FontProvider
import kotlinx.android.synthetic.main.item_recyler_font.view.*

class FontPickerAdapter(
    listFonts: ArrayList<String>,
    val context: Context,
    private val fontSelected: (String) -> Unit
) :
    RecyclerView.Adapter<FontPickerAdapter.ViewHolder>() {

    private val fonts = ArrayList<String>()

    private var currentTextFont = ""

    init {
        fonts.clear()
        fonts.addAll(listFonts)
        currentTextFont = fonts[0]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_recyler_font, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val font = fonts[position]
        val mTypeface =
            if (font.isEmpty()) FontProvider.getDefaultFont()
            else Typeface.createFromAsset(context.assets, font)
        holder.tvFont.typeface = mTypeface
        holder.tvFont.setOnClickListener {
            fontSelected.invoke(font)
            setCurrentTextFont(font)
        }

        if (currentTextFont == font) {
            holder.border.visibility = View.VISIBLE
        } else {
            holder.border.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount() = fonts.size

    fun setCurrentTextFont(currentTextFont: String) {
        Log.d("tagskia", "setCurrentTextFont: $currentTextFont")
        this.currentTextFont = currentTextFont
        notifyDataSetChanged()
    }

    fun getSelectedPos(): Int {
        fonts.forEachIndexed { index, color ->
            if (color == currentTextFont) return index
        }
        return 0
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFont: TextView = view.tv_font
        val border: View = view.border
    }
}