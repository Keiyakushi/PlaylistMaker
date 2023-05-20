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
    private val _state = MutableLiveData<PlayerStatus>()
    val state: LiveData<PlayerStatus> = _state
    private val _SetTime = MutableLiveData <Int>()
    val SetTime : LiveData<Int> = _SetTime


    init {
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        interactor.destroyPlayer()
        mainThreadHandler.removeCallbacksAndMessages(null)
    }

    private fun preparePlayer() {
        interactor.preparePlayer(
            onPrepared = { ->
                _state.postValue(PlayerStatus.OnPrepare)
                mainThreadHandler?.removeCallbacksAndMessages(null)
            },
            onCompletion = { ->
                preparePlayer()
                _state.postValue(PlayerStatus.OnComplete)
            })
    }

    private fun startPlayer(){

        interactor.startPlayer()
        _state.postValue(PlayerStatus.SetPauseImage)
        mainThreadHandler.post(object : Runnable {
            override fun run() {
                val elapsedTime = interactor.getCurrentPosition()
                val remainingTime = interactor.getDuration() - elapsedTime
                if(remainingTime > 0){
                    _SetTime.postValue(interactor.getCurrentPosition())
                    mainThreadHandler?.postDelayed(this, DELAY)
                } else {
                    _state.postValue(PlayerStatus.SetTimeZero)
                }
            }
        })
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

}