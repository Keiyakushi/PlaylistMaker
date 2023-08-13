package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.IMediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.playlist.domain.PlaylistInteractor
import com.practicum.playlistmaker.playlist.domain.PlaylistInteractorImpl
import com.practicum.playlistmaker.search.domain.FavoriteInteractorImpl
import com.practicum.playlistmaker.search.domain.ISearchInteractor
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.domain.db.FavoriteInteractor
import com.practicum.playlistmaker.settings.domain.IRouterInteractor
import com.practicum.playlistmaker.settings.domain.ISettingsInteractor
import com.practicum.playlistmaker.settings.domain.RouterInteractor
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val interactorModule = module {
    singleOf(::MediaPlayerInteractor).bind<IMediaPlayerInteractor>()
    singleOf(::SearchInteractor).bind<ISearchInteractor>()
    singleOf(::SettingsInteractor).bind<ISettingsInteractor>()
    singleOf(::RouterInteractor).bind<IRouterInteractor>()
    singleOf(::FavoriteInteractorImpl).bind<FavoriteInteractor>()
    singleOf(::PlaylistInteractorImpl).bind<PlaylistInteractor>()
}
