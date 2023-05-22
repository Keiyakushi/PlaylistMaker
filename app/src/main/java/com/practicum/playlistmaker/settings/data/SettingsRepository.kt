package com.practicum.playlistmaker.settings.data

import com.practicum.playlistmaker.settings.domain.ISettingsRepository

class SettingsRepository(private val model: ISettingsStorage) : ISettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(
            model.getSettings().isDarkTheme
        )
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        model.saveSettings(
            SettingsModel(settings.darkTheme)
        )
    }
}