package com.practicum.playlistmaker.player.presenter

interface PlayerView {
 fun btPlayAllowed()
 fun btPlaySetImage()
 fun goBack()
 fun btPauseSetImage()
 fun setTimeZero()
 fun getDuration() : Int
 fun setCurrentTime()
 fun getData()
}