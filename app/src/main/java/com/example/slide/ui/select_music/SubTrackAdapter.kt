package com.example.slide.ui.select_music

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.ui.select_music.model.Track
import com.example.slide.util.StringUtils
import kotlinx.android.synthetic.main.item_audio_track.view.*

class SubTrackAdapter(val activity: SelectMusicActivity) :
    RecyclerView.Adapter<SubTrackAdapter.TrackItemHolder>() {

    var tracks = ArrayList<Track>()

    fun updateData(newTracks: ArrayList<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }

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
            //activity.onClicked(EditMusicFragment.getIntent(track))
            activity.trimMusic(track)
        }
    }


    class TrackItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title: TextView = itemView.title

        var artist: TextView = itemView.artist

        var duration: TextView = itemView.tv_duration

        var trackLayout: CardView = itemView.trackLayout

    }
}