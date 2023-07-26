package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.data.Response
import com.practicum.playlistmaker.search.data.Track
import kotlinx.coroutines.flow.Flow


interface ISearchRepository {
    fun loadTracks(query: String): Flow<Resource>
    suspend fun doRequest(query: String): Response
    fun readHistory(): Array<Track>
    fun saveHistory(historyList: ArrayList<Track>)
}
