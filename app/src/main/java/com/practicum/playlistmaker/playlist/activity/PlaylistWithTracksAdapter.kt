package com.practicum.playlistmaker.playlist.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.TrackListViewBinding
import com.practicum.playlistmaker.search.data.Track

class PlaylistWithTracksAdapter(
    private val clickListener: TrackClickListener,
    private val longTrackClickListener: LongTrackClickListener,
) :
    RecyclerView.Adapter<PlaylistWithTracksViewHolder>() {
    var trackAdapterList = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PlaylistWithTracksViewHolder(
            TrackListViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount() = trackAdapterList.size

    override fun onBindViewHolder(holder: PlaylistWithTracksViewHolder, position: Int) {
        holder.bind(trackAdapterList, clickListener, longTrackClickListener)

    }


    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

    fun interface LongTrackClickListener {
        fun LongOnTrackClick(track: Track)
    }
}