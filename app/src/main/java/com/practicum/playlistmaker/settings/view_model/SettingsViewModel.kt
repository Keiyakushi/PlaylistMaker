package com.practicum.playlistmaker.settings.view_model

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.data.ThemeSettings
import com.practicum.playlistmaker.settings.domain.IRouterInteractor
import com.practicum.playlistmaker.settings.domain.ISettingsInteractor

class SettingsViewModel(
    private val interactor: ISettingsInteractor,
    private val router: IRouterInteractor,
) : ViewModel() {

    private var darkTheme = false
    private val _themeSwitcherStateLiveData = MutableLiveData(darkTheme)
    val themeSwitcherStateLiveData: LiveData<Boolean> = _themeSwitcherStateLiveData

    init {
        darkTheme = interactor.getThemeSettings().darkTheme
        _themeSwitcherStateLiveData.postValue(darkTheme)
    }

    fun onThemeSwitcherClicked(isChecked: Boolean) {
        _themeSwitcherStateLiveData.postValue(isChecked)
        interactor.updateThemeSetting(ThemeSettings(isChecked))
        switchTheme(isChecked)
    }

    fun onShareAppClicked() {
        router.shareApp()
    }

    fun onSupportClicked() {
        router.openSupport()
    }

    fun onUserAgreement() {
        router.openTerms()
    }

    private fun switchTheme(darkThemeEnabled: Boolean) {

        darkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
