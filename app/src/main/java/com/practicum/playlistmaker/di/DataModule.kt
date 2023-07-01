package com.practicum.playlistmaker.di

import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.practicum.playlistmaker.application.App
import com.practicum.playlistmaker.player.data.HandlerR
import com.practicum.playlistmaker.router.Router
import com.practicum.playlistmaker.search.data.Network
import com.practicum.playlistmaker.search.data.SearchHistory
import com.practicum.playlistmaker.search.data.iTunesApi
import com.practicum.playlistmaker.settings.data.ISettingsStorage
import com.practicum.playlistmaker.settings.data.SharedPreSettings
import com.practicum.playlistmaker.settings.domain.ISettingsRouter
import com.practicum.playlistmaker.settings.domain.SettingsRouter
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val dataModule = module {
    singleOf(::Network).bind()
    single<iTunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesApi::class.java)
    }
    singleOf(::SearchHistory).bind()
    single {
        androidContext().getSharedPreferences(App.PREFERENCES, AppCompatActivity.MODE_PRIVATE)
    }
    singleOf(::SharedPreSettings).bind<ISettingsStorage>()
    singleOf(::SettingsRouter).bind<ISettingsRouter>()
    factoryOf(::HandlerR).bind()
    factoryOf(::Router).bind()
    factoryOf(::Gson).bind()
}
