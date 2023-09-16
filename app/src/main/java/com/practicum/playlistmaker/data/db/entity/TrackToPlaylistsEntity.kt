package com.practicum.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trackToPlaylist_table")
data class TrackToPlaylistsEntity(
    @PrimaryKey
    val id: Int,
    val previewUrl: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String?,
    val artworkUrl100: String,
    val artworkUrl60: String,
    val collectionName: String,
    val country: String,
    val primaryGenreName: String,
    val releaseDate: String,
    val date: String,
)
