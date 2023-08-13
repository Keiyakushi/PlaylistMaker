package com.practicum.playlistmaker.player.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.BottomSheetListViewBinding
import com.practicum.playlistmaker.playlist.data.Playlist

class PlaylistAdapter(private val clickListener: PlaylistClickListener) :
    RecyclerView.Adapter<PlaylistViewHolder>() {
    var playlist = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlaylistViewHolder(
        BottomSheetListViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = playlist.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlist[position])
        holder.itemView.setOnClickListener { clickListener.onPlaylistClick(playlist[position]) }
    }


    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }

}