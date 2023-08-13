package com.practicum.playlistmaker.playlist.domain

import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.playlist.data.Playlist
import com.practicum.playlistmaker.search.data.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updateTracks(playlist: Playlist)
    fun getSavedPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(playlistId: Int): Flow<PlaylistEntity?>
    fun hasTrack(playlist: Playlist, track: Track): Boolean
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track)
}