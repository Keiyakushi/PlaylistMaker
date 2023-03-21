package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val PREFERENCES = "PREFERENCES"
const val SWITCH_PREFERENCES_KEY = "SWITCH_PREFERENCES_KEY"

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        darkTheme = getSharedPreferences(PREFERENCES, MODE_PRIVATE).getBoolean(
            SWITCH_PREFERENCES_KEY, false
        )
        setMode()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {

        darkTheme = darkThemeEnabled
        setMode()
        getSharedPreferences(PREFERENCES, MODE_PRIVATE)
            .edit()
            .putBoolean(SWITCH_PREFERENCES_KEY, darkThemeEnabled)
            .apply()
    }

    fun setMode(){
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
