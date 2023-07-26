package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.data.Track
import kotlinx.coroutines.flow.Flow

interface ISearchInteractor {
    fun loadTracks(query: String): Flow<Pair<List<Track>?, NetworkError?>>
    fun readHistory(): Array<Track>
    fun saveHistory(historyList: ArrayList<Track>)
}