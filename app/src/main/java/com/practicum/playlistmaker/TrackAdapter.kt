package com.practicum.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private val clickListener: TrackClickListener) :
    RecyclerView.Adapter<TrackViewHolder>() {
    var trackAdapterList = ArrayList<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TrackViewHolder(parent)

    override fun getItemCount() = trackAdapterList.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackAdapterList[position])
        holder.itemView.setOnClickListener{clickListener.onTrackClick(trackAdapterList[position])}
    }


    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }
}
