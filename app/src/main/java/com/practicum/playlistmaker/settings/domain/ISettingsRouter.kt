package com.practicum.playlistmaker.settings.domain

interface ISettingsRouter {
    fun shareLink(shareUrl: String)
    fun openLink(url: String)
    fun openEmail()
}