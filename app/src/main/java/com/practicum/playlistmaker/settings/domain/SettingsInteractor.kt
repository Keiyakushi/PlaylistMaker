package com.practicum.playlistmaker.settings.domain

import com.practicum.playlistmaker.settings.data.ThemeSettings

class SettingsInteractor(private val repository: ISettingsRepository) : ISettingsInteractor {

    override fun getThemeSettings(): ThemeSettings {
        return repository.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        repository.updateThemeSetting(settings)
    }

}
