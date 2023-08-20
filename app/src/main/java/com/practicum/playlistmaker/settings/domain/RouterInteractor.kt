package com.practicum.playlistmaker.settings.domain


class RouterInteractor(
    private val router: ISettingsRouter,
) : IRouterInteractor {
    override fun shareApp(url : String) {
        router.shareLink(url)
    }

    override fun openTerms() {
        router.openLink(getTermsLink())
    }

    override fun openSupport() {
        router.openEmail()
    }

    private fun getTermsLink(): String {
        return "https://yandex.ru/legal/practicum_offer/"
    }
}