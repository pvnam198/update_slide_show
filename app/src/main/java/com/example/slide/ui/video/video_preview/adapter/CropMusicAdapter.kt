package com.example.slide.ui.video.video_preview.adapter

import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.music_engine.CropMusic
import com.example.slide.ui.video.video_preview.MultiMusicPlayingActivity
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.util.StringUtils
import kotlinx.android.synthetic.main.item_selected_music.view.*

class CropMusicAdapter(val activity: MultiMusicPlayingActivity) :
    RecyclerView.Adapter<CropMusicAdapter.ViewHolder>() {

    private val cropMusics = ArrayList<CropMusic>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_selected_music, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = cropMusics.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music = cropMusics[position]
        if (music.defaultMusic != null) {
            holder.tv_title.setText(music.defaultMusic.nameRes)
            holder.tv_duration.text =
                StringUtils.getDurationDisplayFromMillis(music.defaultMusic.duration)
        } else {
            holder.tv_title.text = music.track!!.title
            holder.tv_duration.text = StringUtils.getDurationDisplayFromMillis(music.track.duration)
        }
        if(position == activity.getCurrentMusicPos()) {
            holder.playing_state.visibility = View.VISIBLE
            if (activity.isPlaying()) {
                val animation = ContextCompat.getDrawable(activity, R.drawable.ic_wave) as AnimationDrawable
//                DrawableCompat.setTintList(animation, Color.RED)
                holder.playing_state.setImageDrawable(animation)
                animation.start()
            } else {
                holder.playing_state.setImageResource(R.drawable.ic_wave1)
            }
        } else {
            holder.playing_state.visibility = View.INVISIBLE
        }
        holder.btn_delete.setOnClickListener {
            activity.removeMusicAtPosition(position)
        }
    }

    fun updateMusics() {
        val musics = activity.getSelectedMusic()
        cropMusics.clear()
        cropMusics.addAll(musics)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val btn_delete = view.btn_delete

        val playing_state = view.playing_state

        val tv_duration = view.tv_duration

        val tv_title = view.tv_title

    }
}