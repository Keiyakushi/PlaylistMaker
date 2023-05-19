package com.practicum.playlistmaker.player.data

sealed class PlayerStatus(){
    object OnPrepare: PlayerStatus()
    object OnComplete : PlayerStatus()
    object SetPauseImage : PlayerStatus()

    object SetTimeZero : PlayerStatus()
    object SetPlayImage : PlayerStatus()
}
