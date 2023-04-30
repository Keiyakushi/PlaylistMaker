package com.practicum.playlistmaker.player.presenter

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import com.practicum.playlistmaker.player.MediaActivity
import com.practicum.playlistmaker.player.data.DataPlayer
import com.practicum.playlistmaker.player.data.PlayerRepository
import com.practicum.playlistmaker.player.domain.PlayerState
import java.text.SimpleDateFormat
import java.util.*


class PlayerPresentor(
    private val view:PlayerView,
    private val playerRepository: PlayerRepository
    ) {
    val mainThreadHandler = Handler(Looper.getMainLooper())
    companion object {
        private const val DELAY = 1000L
    }
    fun preparePlayer() {
        view.getData()
        playerRepository.preparePlayer(
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
        playerRepository.startPlayer()
        view.btPauseSetImage()
        mainThreadHandler.postDelayed(object : Runnable {
            override fun run() {
                val elapsedTime = playerRepository.getCurrentPosition()
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
        playerRepository.pausePlayer()
        view.btPlaySetImage()
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }
    fun onBtPlayClicked(){
        when (playerRepository.getPlayerState()) {
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
        playerRepository.destroyPlayer()
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }
    fun goBack(){
        view.goBack()
    }
}