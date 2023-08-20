package com.practicum.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val playlistName: String,
    val playlistDescription: String,
    val imageUrl: String,
    val trackIdList: String,
    val countTracks: Int,
    val saveDate: String,
)