package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.data.Track

class SearchInteractor(
    private val repository: ISearchRepository,
) : ISearchInteractor {
    override fun loadTracks(
        query: String,
        onSuccess: (ArrayList<Track>) -> Unit,
        onError: () -> Unit,
    ) {
        repository.loadTracks(query, onSuccess, onError)
    }

    override fun readHistory(): Array<Track> {
        return repository.readHistory()
    }

    override fun saveHistory(historyList: ArrayList<Track>) {
        repository.saveHistory(historyList)
    }
}
