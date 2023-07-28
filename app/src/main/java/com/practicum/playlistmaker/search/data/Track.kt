package com.practicum.playlistmaker.search.data

data class Track(
    val previewUrl: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String?,
    val artworkUrl100: String,
    val trackId: Int,
    val collectionName: String,
    val country: String,
    val primaryGenreName: String,
    val releaseDate: String,
    var isFavorite: Boolean = false,
){

}