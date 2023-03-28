package com.example.slide.ui.select_image

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.model.Album
import kotlinx.android.synthetic.main.item_folder_layout.view.*

class FolderAdapter(folderList: ArrayList<Album>,
    private val context: Context,
    private val fragment: SelectFragment
) : RecyclerView.Adapter<FolderAdapter.FolderHolder>() {

    private var selectedPosition: Int = 0

    private var folders: ArrayList<Album> = ArrayList()

    init {
        folders.clear()
        folders.addAll(folderList)
    }

    fun updateData(folderList: ArrayList<Album>) {
        folders.clear()
        folders.addAll(folderList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_folder_layout,
            parent, false
        )
        return FolderHolder(view)
    }

    override fun getItemCount() = folders.size

    override fun onBindViewHolder(holder: FolderHolder, position: Int) {
        val album = folders[position]
        Glide.with(context).load(album.imageUrl)
            .into(holder.image_album)
        holder.tv_album_name.text = album.name
        holder.tv_count.text = context.getString(R.string.format_folder_images,album.imageList.size)

        holder.btn_folder.setOnClickListener {
            fragment.onAlbumSelected(position)
            selectedPosition = position
            notifyDataSetChanged()
        }

        /*if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.bg_overlay_image_selected)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_overlay_image_unselected)
        }*/
    }

    class FolderHolder(view: View) : RecyclerView.ViewHolder(view) {

        val image_album = view.image_album

        val tv_album_name = view.tv_album_name

        val tv_count = view.tv_count

        val btn_folder = view.btn_folder
    }
}