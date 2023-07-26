package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.data.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchInteractor(
    private val repository: ISearchRepository,
) : ISearchInteractor {
    override fun loadTracks(query: String): Flow<Pair<List<Track>?, NetworkError?>> {
        return repository
            .loadTracks(query = query)
            .map { resource ->
                when (resource) {
                    is Resource.Success -> {
                        Pair(resource.data, null)
                    }
                    is Resource.Error -> {
                        Pair(null, resource.error)
                    }
                }
            }
    }

    override fun readHistory(): Array<Track> {
        return repository.readHistory()
    }

    override fun saveHistory(historyList: ArrayList<Track>) {
        repository.saveHistory(historyList)
    }
}
