package com.practicum.playlistmaker.player.data

import android.os.Handler
import android.os.Looper

class HandlerR {
    val mainThreadHandler = Handler(Looper.getMainLooper())
    fun get(): Handler {
        return mainThreadHandler
    }
}