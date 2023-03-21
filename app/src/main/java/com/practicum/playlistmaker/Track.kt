package com.practicum.playlistmaker

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val trackId: Int,
    val collectionName: String,
    val country: String,
    val primaryGenreName: String,
    val releaseDate: String,
)