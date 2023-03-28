package com.example.slide.ui.select_music.files

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.ui.select_music.model.MyFile
import java.util.*

class FileAdapter(private var folderFragment: AudioFilesFragment) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    private val files: ArrayList<MyFile> = ArrayList()

    fun updateData(newFiles: List<MyFile>) {
        files.clear()
        files.addAll(newFiles)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val artistAdapterView = LayoutInflater.from(parent.context).inflate(R.layout.item_audio_file, parent, false)
        return FileViewHolder(artistAdapterView)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files.get(position)

        holder.fileNameTextView.text = file.fileName

        holder.songNumberTextView.text = ""
        holder.folderCardView.setOnClickListener {
            if (file.isFolder) {
                folderFragment.showSongsForFolder(file.getUrl())
            } else {
                folderFragment.onFileClick(file)
                notifyDataSetChanged()
            }
        }

        if (file.isFolder) {
            if (file.isSongFolder) {
                holder.folderDesTextView.visibility = View.VISIBLE
                holder.imageView.setImageResource(R.drawable.ic_audio_folder)
            } else {
                holder.folderDesTextView.visibility = View.GONE
                holder.imageView.setImageResource(R.drawable.ic_audio_files)
            }
        } else {
            holder.folderDesTextView.visibility = View.GONE
            holder.imageView.setImageResource(R.drawable.ic_audio_tracks)
        }
    }

    override fun getItemCount(): Int {
        return files.size
    }

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var folderCardView: CardView
        internal var fileNameTextView: TextView
        internal var songNumberTextView: TextView
        internal var imageView: ImageView
        internal var folderDesTextView: TextView


        init {
            folderCardView = itemView.findViewById(R.id.folderCardView)
            fileNameTextView = itemView.findViewById(R.id.folderName)
            songNumberTextView = itemView.findViewById(R.id.songNumber)
            folderDesTextView = itemView.findViewById(R.id.folderDes)
            imageView = itemView.findViewById(R.id.icon_folder)
        }
    }

    fun releaseData() {
        files.clear()
    }
}
