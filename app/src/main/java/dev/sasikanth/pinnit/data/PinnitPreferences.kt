package dev.sasikanth.pinnit.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import dev.sasikanth.pinnit.R
import javax.inject.Inject

class PinnitPreferences @Inject
constructor(
  private val application: Application,
  private val sharedPreferences: SharedPreferences
) {

  companion object {
    const val KEY_THEME = "pref_theme"
  }

  private val defaultThemeValue = application.getString(R.string.pref_theme_auto)
  private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
    when (key) {
      KEY_THEME -> updateUsingThemePreference()
    }
  }

  val theme: Theme
    get() = themePreference
  private var themePreference: Theme
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

  fun changeTheme(theme: Theme) {
    themePreference = theme
  }

  private fun getStorageKeyForTheme(theme: Theme) = when (theme) {
    Theme.DARK -> application.getString(R.string.pref_theme_dark)
    Theme.LIGHT -> application.getString(R.string.pref_theme_light)
    Theme.AUTO -> application.getString(R.string.pref_theme_auto)
  }

  private fun getThemeForStorageValue(value: String) = when (value) {
    application.getString(R.string.pref_theme_dark) -> Theme.DARK
    application.getString(R.string.pref_theme_light) -> Theme.LIGHT
    else -> Theme.AUTO
  }

  private fun updateUsingThemePreference() = when (themePreference) {
    Theme.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    Theme.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    Theme.AUTO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
  }

  enum class Theme {
    LIGHT,
    DARK,
    AUTO
  }
}
