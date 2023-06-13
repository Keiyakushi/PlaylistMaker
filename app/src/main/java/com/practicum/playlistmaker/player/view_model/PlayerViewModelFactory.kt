package com.practicum.playlistmaker.player.view_model

import android.os.Handler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor

class PlayerViewModelFactory(
    private val interactor: MediaPlayerInteractor,
    private val handler: Handler,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerViewModel(interactor, handler) as T
    }
}