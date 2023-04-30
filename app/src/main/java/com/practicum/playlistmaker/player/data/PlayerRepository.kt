package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.IMediaPlayerRepository
import com.practicum.playlistmaker.player.domain.PlayerState

class PlayerRepository(private val url:String) : IMediaPlayerRepository{
    private val mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.STATE_PREPARED
    override fun preparePlayer(onPrepared: () -> Unit,onCompletion: () -> Unit) {
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
    override fun startPlayer(){
        mediaPlayer.start()
        playerState = PlayerState.STATE_PLAYING
    }
    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.STATE_PAUSED
    }
    override fun destroyPlayer(){
        mediaPlayer.release()
    }
    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
    override fun getPlayerState() : PlayerState{
        return playerState
    }
}