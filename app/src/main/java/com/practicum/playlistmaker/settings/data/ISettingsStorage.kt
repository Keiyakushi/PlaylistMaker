package com.practicum.playlistmaker.settings.data

interface ISettingsStorage {
    fun getSettings(): SettingsModel
    fun saveSettings(settingsDto: SettingsModel)
}