package com.practicum.playlistmaker.data.db.entity.dao

import androidx.room.*
import com.practicum.playlistmaker.data.db.entity.TrackEntity


@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("SELECT * FROM track_table")
    suspend fun getTracks(): List<TrackEntity>

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrackEntity(track: TrackEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM track_table WHERE id = :id LIMIT 1);")
    suspend fun isFavorite(id: Int): Boolean
}