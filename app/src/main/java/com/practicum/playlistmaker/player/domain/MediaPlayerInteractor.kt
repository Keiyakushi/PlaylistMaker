package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.player.data.PlayerRepository

class MediaPlayerInteractor(
    private val repository: IMediaPlayerRepository
) : IMediaPlayerInteractor {

    override fun preparePlayer(onPrepared: () -> Unit,onCompletion: () -> Unit){
        repository.preparePlayer(onPrepared,onCompletion)
    }

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun destroyPlayer() {
        repository.destroyPlayer()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

    override fun getPlayerState(): PlayerState {
        return repository.getPlayerState()
    }
}