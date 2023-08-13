package com.practicum.playlistmaker.search.data

import kotlinx.serialization.Serializable

@Serializable
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
) {
    companion object {
        val emptyTrack = Track(
            trackId = 0,
            trackName = "",
            artistName = "",
            trackTimeMillis = "",
            artworkUrl100 = "",
            collectionName = "",
            country = "",
            primaryGenreName = "",
            releaseDate = "",
            previewUrl = "",
        )
    }
}