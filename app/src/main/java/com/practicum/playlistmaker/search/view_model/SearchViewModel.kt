package com.practicum.playlistmaker.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.data.SearchState
import com.practicum.playlistmaker.search.data.Track
import com.practicum.playlistmaker.search.domain.NetworkError
import com.practicum.playlistmaker.search.domain.SearchInteractor
import kotlinx.coroutines.launch

class SearchViewModel(
    private val interactor: SearchInteractor,
    private val historyList: ArrayList<Track>,
) : ViewModel() {
    private val _clearHistoryListLiveData = MutableLiveData<List<Track>>()
    val clearHistoryListLiveData: LiveData<List<Track>> = _clearHistoryListLiveData
    private val _startShowTracks = MutableLiveData<SearchState>()
    val startShowTracks: LiveData<SearchState> = _startShowTracks
    private val _visibilityHistory = MutableLiveData<Boolean>()
    val visibilityHistory: LiveData<Boolean> = _visibilityHistory
    private val _tracksListLiveData = MutableLiveData<List<Track>>()
    val tracksListLiveData: LiveData<List<Track>> = _tracksListLiveData

    override fun onCleared() {
        super.onCleared()
        interactor.saveHistory(historyList)
    }

    fun searchTextClearClicked() {
        _startShowTracks.postValue(SearchState.SearchTextClear)
    }

    fun clearHistory() {
        historyList.clear()
        interactor.saveHistory(historyList)
        _clearHistoryListLiveData.postValue(historyList)
    }

    fun onFocusSearchChanged(hasFocus: Boolean, text: String) {
        if (hasFocus && text.isEmpty() && historyList.isNotEmpty()) {
            _clearHistoryListLiveData.postValue(historyList)
        }
    }

    fun loadTracks(query: String) {
        if (query.isEmpty()) {
            return
        }
        _startShowTracks.postValue(SearchState.PrepareShowTracks)
        viewModelScope.launch {
            interactor
                .loadTracks(query = query)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }
        }
    }

    fun hasTextOnWatcher(query: String) {
        _visibilityHistory.postValue(query.isEmpty())
    }

    fun addAllToHistory(): Array<Track> {
        return interactor.readHistory()
    }

    fun addAllToSaveHistory(historyList: ArrayList<Track>) {
        interactor.saveHistory(this.historyList)
    }

    private fun processResult(data: List<Track>?, error: NetworkError?) {
        when {
            error != null -> {
                if (error == NetworkError.CONNECTION_ERROR) {
                    _startShowTracks.postValue(SearchState.ShowTracksError)
                } else {
                    _startShowTracks.postValue(SearchState.ShowEmptyResult)
                }
            }

            data != null -> {
                _tracksListLiveData.postValue(data)
            }
        }
    }

    fun addTrackToHistory(track: Track) {
        when {
            historyList.contains(track) -> {
                historyList.remove(track)
                historyList.add(0, track)
            }

            historyList.size < 10 -> {
                historyList.add(0, track)
            }

            else -> {
                historyList.removeAt(9)
                historyList.add(0, track)
            }
        }
    }
}