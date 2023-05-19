package com.practicum.playlistmaker.player.view_model

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.data.PlayerStatus
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerState

class PlayerViewModel(
    private val interactor : MediaPlayerInteractor,
    private val mainThreadHandler : Handler
) : ViewModel(){
    companion object {
        private const val DELAY = 1000L
    }
    private val duration : Int = 30000
    private val _state = MutableLiveData<PlayerStatus>()
    val state: LiveData<PlayerStatus> = _state

    override fun onCleared() {
        super.onCleared()
        interactor.pausePlayer()
        interactor.destroyPlayer()
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }

    fun preparePlayer() {
        interactor.preparePlayer(
            onPrepared = { ->
                _state.postValue(PlayerStatus.OnPrepare)
                mainThreadHandler?.removeCallbacksAndMessages(null)
            },
            onCompletion = { ->
                _state.postValue(PlayerStatus.OnComplete)
            })
    }

    fun startPlayer(){
        interactor.startPlayer()
        _state.postValue(PlayerStatus.SetPauseImage)
        mainThreadHandler.postDelayed(object : Runnable {
            override fun run() {
                val elapsedTime = interactor.getCurrentPosition()
                val remainingTime = duration - elapsedTime
                if(remainingTime > 0){
                    _state.postValue(PlayerStatus.SetCurrentTime)
                    mainThreadHandler?.postDelayed(this, DELAY)
                } else {
                    _state.postValue(PlayerStatus.SetTimeZero)
                }
            }
        }, DELAY)
    }
    fun pausePlayer() {
        interactor.pausePlayer()
        _state.postValue(PlayerStatus.SetPlayImage)
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }
    fun onBtPlayClicked(){
        when (interactor.getPlayerState()) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
            }
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
            }
            PlayerState.STATE_DEFAULT -> {
                preparePlayer()
                startPlayer()
            }
        }
    }
    fun destroyPlayer(){
        interactor.destroyPlayer()
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }

}