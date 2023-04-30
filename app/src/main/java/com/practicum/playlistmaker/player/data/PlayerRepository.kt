package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.PlayerState

class PlayerRepository(private val url:String) {
    private val mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.STATE_PREPARED
    fun preparePlayer(onPrepared: () -> Unit,onCompletion: () -> Unit) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPrepared.invoke()
            playerState = PlayerState.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            onCompletion.invoke()
            playerState = PlayerState.STATE_PREPARED
        }
    }
    fun startPlayer(){
        mediaPlayer.start()
        playerState = PlayerState.STATE_PLAYING
    }
    fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.STATE_PAUSED
    }
    fun destroyPlayer(){
        mediaPlayer.release()
    }
    fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
    @JvmName("getPlayerState1")
    fun getPlayerState() : PlayerState{
        return playerState
    }
}