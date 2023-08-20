package com.practicum.playlistmaker.data.db.entity.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.data.db.entity.TrackEntity
import com.practicum.playlistmaker.data.db.entity.TrackToPlaylistsEntity

@Dao
interface TracksToPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TrackToPlaylistsEntity)
}