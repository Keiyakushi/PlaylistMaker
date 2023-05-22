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

}
