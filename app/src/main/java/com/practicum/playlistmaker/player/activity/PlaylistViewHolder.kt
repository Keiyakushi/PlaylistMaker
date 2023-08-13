package com.practicum.playlistmaker.player.activity

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.BottomSheetListViewBinding
import com.practicum.playlistmaker.playlist.data.Playlist

class PlaylistViewHolder(private val binding: BottomSheetListViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(model: Playlist) {
        binding.playlistName.text = model.playlistName
        binding.trackCount.text =
            model.countTracks.toString() + " " + fixNumerical(model.countTracks,
                arrayOf("трек", "трека", "треков"))
        Glide.with(itemView)
            .load(model.imageUrl)
            .placeholder(R.drawable.ic_none_image)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.corner_dp)))
            .into(binding.artwork)
    }
}

private fun fixNumerical(num: Int, text: Array<String>): String {
    return if (num % 100 in 5..20) text[2] else
        if (num % 10 == 1) text[0] else
            if (num % 10 in 2..4) text[1]
            else text[2]
}