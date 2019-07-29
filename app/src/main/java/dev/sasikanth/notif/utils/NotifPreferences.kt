package dev.sasikanth.notif.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import dev.sasikanth.notif.R

class NotifPreferences(
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        const val KEY_THEME = "pref_theme"
    }

    private val defaultThemeValue = context.getString(R.string.pref_theme_light)
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            KEY_THEME -> updateUsingThemePreference()
        }
    }

    var themePreference: Theme
        get() = getThemeForStorageValue(
            sharedPreferences.getString(KEY_THEME, defaultThemeValue)!!
        )
        set(value) {
            sharedPreferences.edit {
                putString(KEY_THEME, getStorageKeyForTheme(value))
            }
        }

    init {
        updateUsingThemePreference()
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    private fun getStorageKeyForTheme(theme: Theme) = when (theme) {
        Theme.DARK -> context.getString(R.string.pref_theme_dark)
        Theme.LIGHT -> context.getString(R.string.pref_theme_light)
    }

    private fun getThemeForStorageValue(value: String) = when (value) {
        context.getString(R.string.pref_theme_dark) -> Theme.DARK
        else -> Theme.LIGHT
    }

    private fun updateUsingThemePreference() = when (themePreference) {
        Theme.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        Theme.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    enum class Theme {
        LIGHT,
        DARK
    }
}
