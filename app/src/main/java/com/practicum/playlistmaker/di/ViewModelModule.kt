package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.view_model.MainViewModel
import com.practicum.playlistmaker.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.data.Track
import com.practicum.playlistmaker.search.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import com.practicum.playlistmaker.media.view_model.FollowTracksViewModel

val viewModelModule = module {
    viewModelOf(::PlayerViewModel).bind()
    viewModel { (historyList: ArrayList<Track>) ->
        SearchViewModel(get(), historyList)
    }
    viewModelOf(::SettingsViewModel).bind()
    viewModelOf(::MainViewModel).bind()
    viewModelOf(::FollowTracksViewModel).bind()
}
