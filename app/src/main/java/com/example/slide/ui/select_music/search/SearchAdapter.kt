package com.example.slide.ui.select_music.search

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.ui.select_music.model.Track
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider
import com.example.slide.util.StringUtils
import kotlinx.android.synthetic.main.item_audio_track.view.*
import java.text.Normalizer
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class SearchAdapter(
    private val itemSelected: (Track) -> Unit,
    private val resultsFound: () -> Unit,
    private val noResultsFound: () -> Unit,
) :
    RecyclerView.Adapter<SearchAdapter.TrackItemHolder>(), Filterable {

    var tracks = ArrayList<Track>()

    var filterTracks = ArrayList<Track>()

    companion object {
        const val TAG = "SearchAdapter"
    }

    init {
        updateData()
    }

    fun updateData() {
        tracks.clear()
        tracks.addAll(LocalMusicProvider.getInstance().allSong)
        filterTracks.clear()
        filterTracks.addAll(LocalMusicProvider.getInstance().allSong)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackItemHolder {
        return TrackItemHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_audio_track, parent, false)
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

        holder.itemView.setOnClickListener {
            itemSelected.invoke(track)
        }

    }

    override fun getFilter(): Filter {
        return ValueFilter()
    }

    private inner class ValueFilter() : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val results = FilterResults()
            try {
                if (!TextUtils.isEmpty(charSequence)) {

                    val trackList = ArrayList<Track>()

                    val searchCharSequence = convertNormalText(charSequence.toString())

                    filterTracks.forEach { track ->

                        val titleTrack = convertNormalText(track.title)

                        val artistTrack = convertNormalText(track.artist)

                        if (titleTrack.contains(searchCharSequence) || artistTrack.contains(
                                searchCharSequence
                            )
                        ) {
                            trackList.add(track)
                        }

                    }
                    results.count = trackList.size
                    results.values = trackList
                } else {
                    results.count = filterTracks.size
                    results.values = filterTracks
                }
            } catch (e: Exception) {
                Log.d(TAG, "performFiltering: ${e.message}")
            }
            return results
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
            try {
                if (filterResults != null) {
                    tracks = filterResults.values as ArrayList<Track>
                    if (tracks.size > 0) {
                        notifyDataSetChanged()
                        resultsFound.invoke()
                    } else
                        noResultsFound.invoke()
                }
            } catch (e: Exception) {
                Log.d(TAG, "publishResults: ${e.message}")
            }
        }

    }

    class TrackItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title: TextView = itemView.title

        var artist: TextView = itemView.artist

        var duration: TextView = itemView.tv_duration

    }

    private fun convertNormalText(str: String): String {
        val nfdNormalizedString: String = Normalizer.normalize(str, Normalizer.Form.NFD)
        val pattern: Pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(nfdNormalizedString).replaceAll("").toUpperCase(Locale.getDefault())
    }

}