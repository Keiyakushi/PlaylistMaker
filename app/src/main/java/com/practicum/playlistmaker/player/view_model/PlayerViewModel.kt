package com.practicum.playlistmaker.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.data.ResultStates
import com.practicum.playlistmaker.media.data.ResultStatesBottomSheet
import com.practicum.playlistmaker.player.data.PlayerStatus
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.playlist.data.Playlist
import com.practicum.playlistmaker.playlist.domain.PlaylistInteractor
import com.practicum.playlistmaker.search.data.Track
import com.practicum.playlistmaker.search.domain.db.FavoriteInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val interactor: MediaPlayerInteractor,
    private val favInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {
    companion object {
        private const val DELAY = 300L
    }

    private val _state = MutableLiveData<PlayerStatus>()
    val state: LiveData<PlayerStatus> = _state
    private val _setTime = MutableLiveData<Int>()
    val setTime: LiveData<Int> = _setTime
    private val _setFollow = MutableLiveData<Boolean>()
    val setFollow = _setFollow
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite = _isFavorite
    private val _toastContent: MutableStateFlow<ResultStatesBottomSheet> =
        MutableStateFlow(ResultStatesBottomSheet.Empty)
    val toastContent = _toastContent
    private val _showContent: MutableStateFlow<ResultStates> = MutableStateFlow(ResultStates.Empty)
    val showContent = _showContent
    private var timerJob: Job? = null
    private var isFavoriteChek: Boolean = false


    override fun onCleared() {
        super.onCleared()
        interactor.destroyPlayer()
        timerJob?.cancel()
    }

    init {
        fillData()
    }

    private fun fillData() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor
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

    fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        if (playlistInteractor.hasTrack(playlist, track)) {
            _toastContent.value = (ResultStatesBottomSheet.AlreadyHas(playlist))
        } else {
            viewModelScope.launch {
                _toastContent.value = (ResultStatesBottomSheet.NotHas(playlist))
                playlistInteractor.addTrackToPlaylist(playlist, track)
            }
        }
    }

    fun preparePlayer(url: String) {
        interactor.preparePlayer(
            onPrepared = { ->
                _state.postValue(PlayerStatus.OnPrepare)
                timerJob?.cancel()
            },
            onCompletion = { ->
                preparePlayer(url)
                _state.postValue(PlayerStatus.OnComplete)
            }, url)
    }

    private fun startPlayer() {

        interactor.startPlayer()
        _state.postValue(PlayerStatus.SetPauseImage)
        timerJob = viewModelScope.launch {
            while (interactor.getPlayerState() == PlayerState.STATE_PLAYING) {
                delay(DELAY)
                _setTime.postValue(interactor.getCurrentPosition())
            }
        }
    }

    fun pausePlayer() {
        interactor.pausePlayer()
        _state.postValue(PlayerStatus.SetPlayImage)
        timerJob?.cancel()
    }

    fun onBtPlayClicked(url: String) {
        when (interactor.getPlayerState()) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
            }
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
            }
            PlayerState.STATE_DEFAULT -> {
                preparePlayer(url)
            }
        }
    }

    fun isFavorite(id: Int) {
        viewModelScope.launch {
            isFavoriteChek = favInteractor.isFavorite(id)
            _isFavorite.postValue(isFavoriteChek)
        }
    }

    fun onBtFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if (isFavoriteChek) {
                favInteractor.deleteTrack(track)
                _setFollow.postValue(true)
            } else {
                favInteractor.addTrack(track)
                _setFollow.postValue(false)
            }
        }
    }
}