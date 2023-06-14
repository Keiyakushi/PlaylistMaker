package com.practicum.playlistmaker.di

import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.application.App
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.practicum.playlistmaker.search.data.Network
import com.practicum.playlistmaker.search.data.SearchHistory
import com.practicum.playlistmaker.settings.data.ISettingsStorage
import com.practicum.playlistmaker.settings.data.SharedPreSettings
import com.practicum.playlistmaker.settings.domain.ISettingsRouter
import com.practicum.playlistmaker.settings.domain.SettingsRouter
import com.practicum.playlistmaker.player.data.HandlerR
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind


val dataModule = module {
    singleOf(::Network).bind()
    singleOf(::SearchHistory).bind()
    single {
        androidContext().getSharedPreferences(App.PREFERENCES, AppCompatActivity.MODE_PRIVATE)
    }
    singleOf(::SharedPreSettings).bind<ISettingsStorage>()
    singleOf(::SettingsRouter).bind<ISettingsRouter>()
    singleOf(::HandlerR).bind()
}
