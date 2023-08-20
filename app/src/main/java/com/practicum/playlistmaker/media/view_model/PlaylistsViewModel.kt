package com.practicum.playlistmaker.media.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.data.ResultStates
import com.practicum.playlistmaker.playlist.data.Playlist
import com.practicum.playlistmaker.playlist.domain.PlaylistInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val interactor: PlaylistInteractor) : ViewModel() {
    private val _showContent: MutableStateFlow<ResultStates> = MutableStateFlow(ResultStates.Empty)
    val showContent = _showContent

    init {
        fillData()
    }

    private fun fillData() {
        viewModelScope.launch(Dispatchers.IO) {
            interactor
                .getSavedPlaylists()
                .collect {
                    result(it)
                }
        }
    }

    fun result(playlist: List<Playlist>) {
        if (playlist.isEmpty()) {
            _showContent.value = (ResultStates.Empty)
        } else {
            _showContent.value = (ResultStates.HasPlaylists(playlist))
        }
    }
}