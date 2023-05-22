package com.practicum.playlistmaker.search.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.search.data.Track
import com.practicum.playlistmaker.search.domain.SearchInteractor

class SearchViewModelFactory(
    private val interactor: SearchInteractor,
    private val historyList: ArrayList<Track>,
) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(interactor, historyList) as T
    }
}