package com.practicum.playlistmaker.search.domain.db

import com.practicum.playlistmaker.search.data.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {
    suspend fun addTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun isFavorite(id: Int): Boolean
}