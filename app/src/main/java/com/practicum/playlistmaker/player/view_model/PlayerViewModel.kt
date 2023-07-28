package com.practicum.playlistmaker.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.data.PlayerStatus
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.search.data.Track
import com.practicum.playlistmaker.search.domain.db.FavoriteInteractor
import kotlinx.coroutines.*

class PlayerViewModel(
    private val interactor: MediaPlayerInteractor,
    private val favInteractor : FavoriteInteractor,
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
    private var timerJob: Job? = null
    private var isFavoriteChek: Boolean = false


    override fun onCleared() {
        super.onCleared()
        interactor.destroyPlayer()
        timerJob?.cancel()
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
        viewModelScope.launch() {
            isFavoriteChek = favInteractor.isFavorite(id)
            _isFavorite.postValue(isFavoriteChek)
        }
    }
    fun onBtFavoriteClicked(track:Track){
        viewModelScope.launch() {
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