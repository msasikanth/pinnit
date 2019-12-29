package dev.sasikanth.pinnit.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import dev.sasikanth.pinnit.R
import javax.inject.Inject

class PinnitPreferences
@Inject constructor(
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) {

  companion object {
    const val KEY_THEME = "pref_theme"
    const val KEY_ALLOWED_APPS = "allowed_apps"
  }

  private val defaultThemeValue = context.getString(R.string.pref_theme_auto)
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

  val allowedApps: Set<String>
    get() = _allowedApps
  private var _allowedApps: Set<String>
    get() = sharedPreferences.getStringSet(KEY_ALLOWED_APPS, setOf()).orEmpty()
    set(value) {
      sharedPreferences.edit {
        putStringSet(KEY_ALLOWED_APPS, value)
      }
    }

  init {
    updateUsingThemePreference()
    sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
  }

  fun updateAllowedApps(allowedApps: Set<String>) {
    _allowedApps = allowedApps
  }

  fun changeTheme(theme: Theme) {
    themePreference = theme
  }

  private fun getStorageKeyForTheme(theme: Theme) = when (theme) {
    Theme.DARK -> context.getString(R.string.pref_theme_dark)
    Theme.LIGHT -> context.getString(R.string.pref_theme_light)
    Theme.AUTO -> context.getString(R.string.pref_theme_auto)
  }

  private fun getThemeForStorageValue(value: String) = when (value) {
    context.getString(R.string.pref_theme_dark) -> Theme.DARK
    context.getString(R.string.pref_theme_light) -> Theme.LIGHT
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
