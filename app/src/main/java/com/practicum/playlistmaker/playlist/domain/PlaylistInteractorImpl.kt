package com.practicum.playlistmaker.playlist.domain

import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.playlist.data.Playlist
import com.practicum.playlistmaker.search.data.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {
    override suspend fun insertPlaylist(playlist: Playlist) {
        repository.insertPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override suspend fun updateTracks(playlist: Playlist) {
        repository.updateTracks(playlist)
    }

    override fun getSavedPlaylists(): Flow<List<Playlist>> {
        return repository.getSavedPlaylists()
    }

    override fun getPlaylistById(playlistId: Int): Flow<PlaylistEntity?> {
        return repository.getPlaylistById(playlistId)
    }

    override fun hasTrack(playlist: Playlist, track: Track): Boolean {
        return playlist.trackList.contains(track.trackId.toString())
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        playlist.trackList = playlist.trackList + track.trackId
        playlist.countTracks = playlist.countTracks.inc()
        repository.updateTracks(playlist)
    }

    override suspend fun insertTrackToPlaylists(track: Track) {
        repository.insertTrackToPlaylists(track)
    }

}