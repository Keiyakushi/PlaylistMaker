package com.practicum.playlistmaker.playlist.data

import android.util.Log
import com.practicum.playlistmaker.data.db.entity.AppDatabase
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.data.db.entity.TrackDbConvertor
import com.practicum.playlistmaker.playlist.domain.PlaylistRepository
import com.practicum.playlistmaker.search.data.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : PlaylistRepository {
    override suspend fun insertPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(trackDbConvertor.map(playlist))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().deletePlaylist(trackDbConvertor.map(playlist))
    }

    override suspend fun updateTracks(playlist: Playlist) {
        appDatabase.playlistDao().updateTracks(trackDbConvertor.map(playlist))
    }

    override fun getSavedPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getSavedPlaylists().map { convertFromTracksEntity(it) }
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist> {
        return appDatabase.playlistDao().getPlaylistById(playlistId).map { trackDbConvertor.map(it) }
    }

    override suspend fun insertTrackToPlaylists(track: Track) {
        appDatabase.tracksToPlaylistDao().insertTrack(trackDbConvertor.mapTrackToPlaylist(track))
    }

    private fun convertFromTracksEntity(playlist: List<PlaylistEntity>): List<Playlist> {
        return playlist.map { playlist -> trackDbConvertor.map(playlist) }
    }
}