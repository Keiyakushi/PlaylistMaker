package com.practicum.playlistmaker.playlist.view_model

import android.Manifest
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import com.practicum.playlistmaker.playlist.data.PermissionStates
import com.practicum.playlistmaker.playlist.data.Playlist
import com.practicum.playlistmaker.playlist.domain.PlaylistInteractor
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(private val interactor: PlaylistInteractor) : ViewModel() {
    private var imageUrl = ""
    private var playlistName = ""
    private var playlistDescription = ""
    private val register = PermissionRequester.instance()
    private val _permissionStateFlow: MutableSharedFlow<PermissionStates> = MutableSharedFlow(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val permissionStateFlow = _permissionStateFlow.asSharedFlow()
    private val _allowedToBack = MutableLiveData<Boolean>()
    val allowedToBack = _allowedToBack

    fun onImageClicked() {
        viewModelScope.launch {
            if (Build.VERSION.SDK_INT >= 33) {
                register.request(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                register.request(Manifest.permission.READ_EXTERNAL_STORAGE)
            }.collect { result ->
                when (result) {
                    is PermissionResult.Granted -> {
                        _permissionStateFlow.emit(PermissionStates.GRANTED)
                    }
                    is PermissionResult.Denied.NeedsRationale -> {

                    }
                    is PermissionResult.Denied.DeniedPermanently -> {
                        _permissionStateFlow.emit(PermissionStates.DENIED_PERMANENTLY)
                    }
                    PermissionResult.Cancelled -> return@collect
                }
            }
        }
    }

    fun savePlaylistName(name: String) {
        playlistName = name
    }

    fun savePlaylistDescription(description: String) {
        playlistDescription = description
    }

    fun saveImageUri(uri: String) {
        imageUrl = uri
    }

    fun onCreateBtClicked(playlist: Playlist?) {
        if (playlist == null) {
            viewModelScope.launch {
                interactor.insertPlaylist(Playlist(
                    id = 0,
                    imageUrl = imageUrl,
                    playlistName = playlistName,
                    playlistDescription = playlistDescription,
                    trackList = emptyList(),
                    countTracks = 0
                ))
            }
        }else{
            viewModelScope.launch {
                interactor.updateTracks(Playlist(
                    id = playlist.id,
                    playlistName = playlistName,
                    playlistDescription = playlistDescription,
                    imageUrl = imageUrl,
                    trackList = playlist.trackList,
                    countTracks = playlist.countTracks,
                ))
            }
        }
    }

    fun onBackPressed(playlist: Playlist?) {
        if (playlist == null) {
            if (imageUrl.isNotEmpty() || playlistName.isNotEmpty() || playlistDescription.isNotEmpty()) {
                _allowedToBack.postValue(false)
            } else {
                _allowedToBack.postValue(true)
            }
        } else {
            _allowedToBack.postValue(true)
        }
    }
}