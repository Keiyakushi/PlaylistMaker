package com.practicum.playlistmaker.data.db.entity.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.practicum.playlistmaker.data.db.entity.TrackEntity
import com.practicum.playlistmaker.data.db.entity.TrackToPlaylistsEntity

@Dao
interface TracksToPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TrackToPlaylistsEntity)

    @Delete(entity = TrackToPlaylistsEntity::class)
    suspend fun deleteTrackEntity(track: TrackToPlaylistsEntity)
}