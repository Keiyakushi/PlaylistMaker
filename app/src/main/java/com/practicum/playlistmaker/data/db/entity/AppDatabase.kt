package com.practicum.playlistmaker.data.db.entity

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.data.db.entity.dao.PlaylistDao
import com.practicum.playlistmaker.data.db.entity.dao.TrackDao
import com.practicum.playlistmaker.data.db.entity.dao.TracksToPlaylistDao

@Database(version = 2,
    entities = [TrackEntity::class, PlaylistEntity::class, TrackToPlaylistsEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackDao(): TrackDao
    abstract fun tracksToPlaylistDao(): TracksToPlaylistDao
}