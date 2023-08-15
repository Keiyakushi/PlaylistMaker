package com.practicum.playlistmaker.playlist.domain

import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.playlist.data.Playlist
import com.practicum.playlistmaker.search.data.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updateTracks(playlist: Playlist)
    fun getSavedPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(playlistId: Int): Flow<PlaylistEntity?>
    suspend fun insertTrackToPlaylists(track: Track)
}