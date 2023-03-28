package com.example.slide.ui.select_music.folder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider

class FolderAdapter(val fragment: AudioFoldersFragment) : RecyclerView.Adapter<FolderAdapter.TrackItemHolder>() {

    var folders = LocalMusicProvider.getInstance().folders


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackItemHolder {
        return TrackItemHolder(LayoutInflater.from(fragment.requireContext()).inflate(R.layout.item_audio_folder, parent, false))
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    override fun onBindViewHolder(holder: TrackItemHolder, position: Int) {
        val folder = folders[position]
        holder.folderName.text = folder.name
        holder.songNumber.text = folder.songNumber.toString()
        holder.folderCardView.setOnClickListener{
            fragment.gotoFolder(folder)
        }
    }

    class TrackItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var folderName: TextView
        var songNumber: TextView

        var folderIcon: ImageView

        var folderCardView: CardView

        init {
            folderName = itemView.findViewById(R.id.folderName)
            songNumber = itemView.findViewById(R.id.songNumber)

            folderIcon = itemView.findViewById(R.id.icon_folder)
            folderCardView = itemView.findViewById(R.id.folderCardView)

        }
    }
}