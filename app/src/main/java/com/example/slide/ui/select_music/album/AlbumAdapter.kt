package com.example.slide.ui.select_music.album

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.ui.select_music.model.MusicAlbum
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider

class AlbumAdapter(val fragment: AudioAlbumFragment) :
    RecyclerView.Adapter<AlbumAdapter.TrackItemHolder>() {

    private var albums = ArrayList<MusicAlbum>()

    init {
        albums.clear()
        albums.addAll(LocalMusicProvider.getInstance().albums)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackItemHolder {
        return TrackItemHolder(
            LayoutInflater.from(fragment.requireContext())
                .inflate(R.layout.item_audio_album, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    override fun onBindViewHolder(holder: TrackItemHolder, position: Int) {
        val album = albums[position]
        holder.name.text = album.name

        val albumArtist =
            if (album.artist != null) album.artist else holder.itemView.context.getString(R.string.unknown_artist_album)

        val songNumber = trimArtist(albumArtist) + " - " + fragment.getString(
            R.string.song_number_format,
            album.songNumber
        )

        holder.songNumber.text = songNumber
        holder.btn_album.setOnClickListener {
            fragment.goToAlbum(album)
        }
        if (!TextUtils.isEmpty(album.arlUrl))
            Glide.with(fragment).load(album.arlUrl).placeholder(R.drawable.ic_audio_album)
                .into(holder.albumArt)
    }

    fun trimArtist(artist: String): String {
        if (artist.length > 30) return artist.substring(0, 27) + "..."
        return artist

    }

    class TrackItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name: TextView = itemView.findViewById(R.id.albumName)

        var songNumber: TextView = itemView.findViewById(R.id.songNumber)

        var albumArt: ImageView = itemView.findViewById(R.id.albumImage)

        var btn_album: View = itemView.findViewById(R.id.btn_album)

    }
}