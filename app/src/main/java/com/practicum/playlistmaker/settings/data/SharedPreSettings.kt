package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences

class SharedPreSettings(private val sharedPreferences: SharedPreferences) :
    ISettingsStorage {

    companion object {
        const val SWITCH_THEME_KEY = "SWITCH_PREFERENCES_KEY"
    }

    override fun getSettings(): SettingsModel {
        return SettingsModel(
            sharedPreferences.getBoolean(SWITCH_THEME_KEY, false)
        )
    }

    override fun saveSettings(settingsDto: SettingsModel) {
        sharedPreferences
            .edit()
            .putBoolean(SWITCH_THEME_KEY, settingsDto.isDarkTheme)
            .apply()
    }
}