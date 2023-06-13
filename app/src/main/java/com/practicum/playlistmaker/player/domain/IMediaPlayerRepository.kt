package com.practicum.playlistmaker.player.domain

interface IMediaPlayerRepository {
    fun preparePlayer(onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun destroyPlayer()
    fun getDuration(): Int
    fun getCurrentPosition(): Int
    fun getPlayerState(): PlayerState
}