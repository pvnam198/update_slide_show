package com.example.slide.ui.select_music.track

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.ui.select_music.SelectMusicActivity
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider
import com.example.slide.util.StringUtils
import com.l4digital.fastscroll.FastScroller
import kotlinx.android.synthetic.main.item_audio_track.view.*
import java.util.*

class TrackAdapter(val activity: SelectMusicActivity) :
    RecyclerView.Adapter<TrackAdapter.TrackItemHolder>(),
    FastScroller.SectionIndexer {

    var tracks = LocalMusicProvider.getInstance().allSong


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackItemHolder {
        return TrackItemHolder(
            LayoutInflater.from(activity).inflate(R.layout.item_audio_track, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackItemHolder, position: Int) {
        val track = tracks[position]
        holder.title.text = track.title
        holder.artist.text = track.artist
        holder.duration.text = StringUtils.getDurationDisplayFromMillis(track.duration)

        holder.trackLayout.setOnClickListener {
            activity.trimMusic(track)
        }
    }


    class TrackItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title: TextView = itemView.title

        var artist: TextView = itemView.artist

        var duration: TextView = itemView.tv_duration

        var trackLayout: CardView = itemView.trackLayout

    }

    override fun getSectionText(position: Int): CharSequence {
        return tracks[position].title.substring(0, 1).toUpperCase(Locale.getDefault())
    }
}