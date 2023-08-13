package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.data.db.entity.TrackDbConvertor
import com.practicum.playlistmaker.player.data.PlayerRepository
import com.practicum.playlistmaker.player.domain.IMediaPlayerRepository
import com.practicum.playlistmaker.playlist.data.PlaylistRepositoryImpl
import com.practicum.playlistmaker.playlist.domain.PlaylistRepository
import com.practicum.playlistmaker.search.data.FavoriteRepositoryImpl
import com.practicum.playlistmaker.search.data.SearchRepository
import com.practicum.playlistmaker.search.domain.ISearchRepository
import com.practicum.playlistmaker.search.domain.db.FavoriteRepository
import com.practicum.playlistmaker.settings.data.SettingsRepository
import com.practicum.playlistmaker.settings.domain.ISettingsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val repositoryModule = module {
    singleOf(::PlayerRepository).bind<IMediaPlayerRepository>()
    singleOf(::SearchRepository).bind<ISearchRepository>()
    singleOf(::SettingsRepository).bind<ISettingsRepository>()
    single { TrackDbConvertor() }
    single<FavoriteRepository> { FavoriteRepositoryImpl(get(), get()) }
    single<PlaylistRepository> { PlaylistRepositoryImpl(get(), get()) }
}
