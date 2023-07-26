package com.practicum.playlistmaker.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.data.PlayerStatus
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val interactor: MediaPlayerInteractor,
) : ViewModel() {
    companion object {
        private const val DELAY = 300L
    }

    private val _state = MutableLiveData<PlayerStatus>()
    val state: LiveData<PlayerStatus> = _state
    private val _SetTime = MutableLiveData<Int>()
    val SetTime: LiveData<Int> = _SetTime
    private var timerJob: Job? = null


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
                    _SetTime.postValue(interactor.getCurrentPosition())
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

}