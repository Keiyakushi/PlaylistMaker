package com.practicum.playlistmaker.settings.data

import com.practicum.playlistmaker.settings.data.SettingsModel

interface ISettingsStorage {
    fun getSettings(): SettingsModel
    fun saveSettings(settingsDto: SettingsModel)
}