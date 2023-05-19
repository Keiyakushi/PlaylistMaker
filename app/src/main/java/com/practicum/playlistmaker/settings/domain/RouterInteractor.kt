package com.practicum.playlistmaker.settings.domain

class RouterInteractor (
    private val router: ISettingsRouter
        ) : IRouterInteractor{
    override fun shareApp() {
        router.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        router.openLink(getTermsLink())
    }

    override fun openSupport() {
        router.openEmail()
    }

    private fun getShareAppLink(): String {
        return "https://practicum.yandex.ru/android-developer/"
    }

    private fun getTermsLink(): String {
        return "https://yandex.ru/legal/practicum_offer/"
    }
}