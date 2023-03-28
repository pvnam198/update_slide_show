package com.example.slide.ui.select_music.default

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.music_engine.DefaultMusic
import com.example.slide.util.StringUtils
import kotlinx.android.synthetic.main.activity_select_music.view.*
import kotlinx.android.synthetic.main.item_audio_track.view.*

class DefaultMusicAdapter(private val tracks: ArrayList<DefaultMusic>, private val context: Context, private val onItemClick:(DefaultMusic) -> Unit) :
    RecyclerView.Adapter<DefaultMusicAdapter.TrackHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_audio_track, parent, false)
        return TrackHolder(view)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        val track = tracks[position]
        holder.title.setText(track.nameRes)
        holder.tvDuration.text = StringUtils.getDurationDisplayFromMillis(track.duration)

        holder.itemView.setOnClickListener { onItemClick.invoke(track) }
    }

    override fun getItemCount() = tracks.size

    class TrackHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.title
        val artist: TextView = view.artist
        val tvDuration: TextView = view.tv_duration
    }

}
