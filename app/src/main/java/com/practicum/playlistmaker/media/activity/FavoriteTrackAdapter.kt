package com.practicum.playlistmaker.media.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.TrackListViewBinding
import com.practicum.playlistmaker.search.activity.TrackViewHolder
import com.practicum.playlistmaker.search.data.Track

class FavoriteTrackAdapter(private val clickListener: TrackClickListener) :
    RecyclerView.Adapter<TrackViewHolder>() {
    var trackAdapterList = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TrackViewHolder(
        TrackListViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = trackAdapterList.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackAdapterList[position])
        holder.itemView.setOnClickListener { clickListener.onTrackClick(trackAdapterList[position]) }
    }


    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

}