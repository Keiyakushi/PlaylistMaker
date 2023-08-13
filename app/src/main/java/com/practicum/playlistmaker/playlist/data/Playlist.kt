package com.practicum.playlistmaker.playlist.data

import com.practicum.playlistmaker.search.data.Track
import kotlinx.serialization.Serializable

data class Playlist(
    val id: Int,
    val playlistName: String,
    val playlistDescription: String,
    val imageUrl: String,
    @Serializable
    var trackList: List<Track>,
    var countTracks: Int,
)
