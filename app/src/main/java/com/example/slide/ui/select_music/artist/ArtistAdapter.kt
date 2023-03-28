package com.example.slide.ui.select_music.artist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider
import kotlinx.android.synthetic.main.item_audio_artist.view.*

class ArtistAdapter(val fragment: AudioArtistFragment) :
    RecyclerView.Adapter<ArtistAdapter.TrackItemHolder>() {

    var artists = LocalMusicProvider.getInstance().artists

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackItemHolder {
        return TrackItemHolder(
            LayoutInflater.from(fragment.context).inflate(
                R.layout.item_audio_artist,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return artists.size
    }

    override fun onBindViewHolder(holder: TrackItemHolder, position: Int) {
        val artist = artists[position]
        holder.artistName.text = artist.name
        holder.songNumber.text = fragment.requireContext().getString(R.string.song_number_format, artist.songNumber)
        if (artist.art.isNotEmpty())
            Glide.with(fragment.requireContext()).load(artist.art).placeholder(R.drawable.ic_audio_artists).into(holder.iv_artist)

        holder.itemView.setOnClickListener {
            fragment.gotoArtist(artist)
        }
    }


    class TrackItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_artist: ImageView = itemView.iv_artist
        var card: CardView = itemView.card
        var artistName: TextView = itemView.tv_artist_name
        var songNumber: TextView = itemView.songNumber

    }
}