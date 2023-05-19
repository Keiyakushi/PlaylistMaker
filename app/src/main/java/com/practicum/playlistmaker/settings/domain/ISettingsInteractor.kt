package com.practicum.playlistmaker.settings.domain

import com.practicum.playlistmaker.settings.data.ThemeSettings

interface ISettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}