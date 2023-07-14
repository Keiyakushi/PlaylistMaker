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
    private val _ClearHistoryListLiveData = MutableLiveData<List<Track>>()
    val ClearHistoryListLiveData: LiveData<List<Track>> = _ClearHistoryListLiveData
    private val _StartShowTracks = MutableLiveData<SearchState>()
    val StartShowTracks: LiveData<SearchState> = _StartShowTracks
    private val _VisbilityHistory = MutableLiveData<Boolean>()
    val VisbilityHistory: LiveData<Boolean> = _VisbilityHistory
    private val _TracksListLiveData = MutableLiveData<List<Track>>()
    val TracksListLiveData: LiveData<List<Track>> = _TracksListLiveData

    override fun onCleared() {
        super.onCleared()
        interactor.saveHistory(historyList)
    }

    fun SearchTextClearClicked() {
        _StartShowTracks.postValue(SearchState.SearchTextClear)
    }

    fun clearHistory() {
        historyList.clear()
        interactor.saveHistory(historyList)
        _ClearHistoryListLiveData.postValue(historyList)
    }

    fun onFocusSearchChanged(hasFocus: Boolean, text: String) {
        if (hasFocus && text.isEmpty() && historyList.isNotEmpty()) {
            _ClearHistoryListLiveData.postValue(historyList)
        }
    }

    fun loadTracks(query: String) {
        if (query.isEmpty()) {
            return
        }
        _StartShowTracks.postValue(SearchState.PrepareShowTracks)
        viewModelScope.launch {
            interactor
                .loadTracks(query = query)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }
        }
    }

    fun hasTextOnWatcher(query: String) {
        _VisbilityHistory.postValue(query.isEmpty())
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
                _StartShowTracks.postValue(SearchState.ShowTracksError)
            }

            data != null -> {
                if (data.isEmpty()) {
                    _StartShowTracks.postValue(SearchState.ShowEmptyResult)
                } else {
                    _TracksListLiveData.postValue(data)
                }
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