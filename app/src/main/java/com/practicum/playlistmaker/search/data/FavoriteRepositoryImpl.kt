package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.data.db.entity.AppDatabase
import com.practicum.playlistmaker.data.db.entity.TrackDbConvertor
import com.practicum.playlistmaker.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.db.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavoriteRepository {
    override suspend fun addTrack(track: Track) {
        appDatabase.trackDao().insertTrack(trackDbConvertor.map(track))
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.trackDao().deleteTrackEntity(trackDbConvertor.map(track))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTracksEntity(tracks))
    }

    override suspend fun isFavorite(id: Int): Boolean {
        return appDatabase.trackDao().isFavorite(id)
    }

    private fun convertFromTracksEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}