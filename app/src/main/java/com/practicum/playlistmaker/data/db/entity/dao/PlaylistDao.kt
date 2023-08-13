package com.practicum.playlistmaker.data.db.entity.dao

import androidx.room.*
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updateTracks(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists_table ORDER BY saveDate DESC;")
    fun getSavedPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists_table WHERE id = :playlistId")
    fun getPlaylistById(playlistId: Int): Flow<PlaylistEntity?>
}