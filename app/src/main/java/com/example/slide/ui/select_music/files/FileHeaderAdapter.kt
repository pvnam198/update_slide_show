package com.example.slide.ui.select_music.files

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.util.MyStatic
import java.io.File
import java.util.*

class FileHeaderAdapter(private var fragment: AudioFilesFragment) :
    RecyclerView.Adapter<FileHeaderAdapter.FileViewHolder>() {

    private val files: ArrayList<File> = ArrayList()

    fun updateData(nFiles: List<File>) {
        files.clear()
        files.addAll(nFiles)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val artistAdapterView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_audio_file_header, parent, false)
        return FileViewHolder(artistAdapterView)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files.get(position)
        if (file.path == MyStatic.EXTERNAL_STORAGE_PATH) {
            holder.tv_title.setText(R.string.sd_card)
        } else {
            holder.tv_title.text = file.name
        }

        holder.btn_file_header.setOnClickListener {
            fragment.showSongsForFolder(file.absolutePath)
        }

        if (position == itemCount - 1) {
            holder.tv_title.setTextColor(Color.WHITE)
            holder.iv_next.setColorFilter(Color.WHITE)
        } else {
            holder.iv_next.setColorFilter(
                ContextCompat.getColor(
                    fragment.requireContext(),
                    R.color.itemTextLevel1
                )
            )
            holder.tv_title.setTextColor(
                ContextCompat.getColor(
                    fragment.requireContext(),
                    R.color.itemTextLevel1
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return files.size
    }

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tv_title: TextView = itemView.findViewById(R.id.tv_title)

        val iv_next: ImageView = itemView.findViewById(R.id.iv_next)

        val btn_file_header: View = itemView.findViewById(R.id.btn_file_header)

    }

    fun releaseData() {
        files.clear()
    }
}
