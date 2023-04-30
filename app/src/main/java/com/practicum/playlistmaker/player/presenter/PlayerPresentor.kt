package com.practicum.playlistmaker.player.presenter

import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.player.data.PlayerRepository
import com.practicum.playlistmaker.player.domain.IMediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.search.domain.SearchInteractor


class PlayerPresentor(
    private val view:PlayerView,
    private val interactor: IMediaPlayerInteractor
    ) {
    val mainThreadHandler = Handler(Looper.getMainLooper())
    companion object {
        private const val DELAY = 1000L
    }
    fun preparePlayer() {
        view.getData()
        interactor.preparePlayer(
        onPrepared = { ->
            view.btPlayAllowed()
            mainThreadHandler?.removeCallbacksAndMessages(null)
        },
        onCompletion = { ->
            view.btPlaySetImage()
            view.setTimeZero()
        })
    }
    fun startPlayer(){
        interactor.startPlayer()
        view.btPauseSetImage()
        mainThreadHandler.postDelayed(object : Runnable {
            override fun run() {
                val elapsedTime = interactor.getCurrentPosition()
                val remainingTime = view.getDuration() - elapsedTime
                if(remainingTime > 0){
                    view.setCurrentTime()
                    mainThreadHandler?.postDelayed(this, DELAY)
                } else {
                    view.setTimeZero()
                }
            }
        },DELAY)
    }
    fun pausePlayer() {
        interactor.pausePlayer()
        view.btPlaySetImage()
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
    fun goBack(){
        view.goBack()
    }
}