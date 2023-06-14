package com.practicum.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.application.App
import com.practicum.playlistmaker.search.data.*
import com.practicum.playlistmaker.settings.data.ISettingsStorage
import com.practicum.playlistmaker.settings.data.SettingsRepository
import com.practicum.playlistmaker.settings.data.SharedPreSettings
import com.practicum.playlistmaker.settings.domain.*

object Creator {
    fun provideSettingsInteractor(context: Context): ISettingsInteractor {
        return SettingsInteractor(getSettingsRepository(context))
    }

    fun provideRouterInteractor(context: Context): IRouterInteractor {
        return RouterInteractor(getRouter(context))
    }

    fun provideSearchHistory(context: Context): SearchHistory {
        return SearchHistory(context.getSharedPreferences
            (App.PREFERENCES, AppCompatActivity.MODE_PRIVATE))
    }


    private fun getSettingsRepository(context: Context): ISettingsRepository {
        return SettingsRepository(getSettingsStorage(context))
    }

    private fun getRouter(context: Context): ISettingsRouter {
        return SettingsRouter(context)
    }

    private fun getSettingsStorage(context: Context): ISettingsStorage {
        return SharedPreSettings(getSharedPreferences(context))
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(App.PREFERENCES, AppCompatActivity.MODE_PRIVATE)
    }

}