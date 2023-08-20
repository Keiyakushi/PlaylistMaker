package com.practicum.playlistmaker.playlist.activity

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistListViewBinding
import com.practicum.playlistmaker.databinding.TrackListViewBinding

import com.practicum.playlistmaker.playlist.data.Playlist
import com.practicum.playlistmaker.search.data.Track
import java.text.SimpleDateFormat
import java.util.*

class PlaylistWithTracksViewHolder(private val binding: TrackListViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(itemList : List<Track>,
             clickListener: PlaylistWithTracksAdapter.TrackClickListener,
             longClickListener: PlaylistWithTracksAdapter.LongTrackClickListener) {
        val model : Track = itemList[adapterPosition]
        binding.trackName.text = model.trackName
        binding.artistName.text = model.artistName
        binding.trackTime.text = (if (model.trackTimeMillis == null) {
            ""
        } else {
            SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(model.trackTimeMillis.toLong())
        }).toString()
        Glide.with(itemView)
            .load(model.artworkUrl60)
            .placeholder(R.drawable.ic_none_image)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.corner_dp)))
            .into(binding.artwork)
        itemView.setOnClickListener { clickListener.onTrackClick(model) }
        itemView.setOnLongClickListener { longClickListener.LongOnTrackClick(model)
            return@setOnLongClickListener true}
    }
}