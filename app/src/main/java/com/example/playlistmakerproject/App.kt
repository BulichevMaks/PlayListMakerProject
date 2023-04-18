package com.example.playlistmakerproject

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val PREFERENCES = "practicum_example_preferences"
const val EDIT_TEXT_KEY = "key_for_edit_text"

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(EDIT_TEXT_KEY, darkTheme)
            .apply()

        switchTheme(sharedPrefs.getBoolean(EDIT_TEXT_KEY, true))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
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