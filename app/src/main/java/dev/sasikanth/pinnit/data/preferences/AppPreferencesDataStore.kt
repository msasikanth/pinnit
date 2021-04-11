package dev.sasikanth.pinnit.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.migrations.SharedPreferencesView
import dev.sasikanth.pinnit.BuildConfig
import dev.sasikanth.pinnit.R

private const val PREFERENCES_NAME = "${BuildConfig.APPLICATION_ID}_preferences"
private const val DATA_STORE_FILE_NAME = "app_prefs.pb"
private const val KEY_THEME = "pref_theme"
private const val KEY_OEM_WARNING_DIALOG = "pref_oem_warning_dialog"

val Context.appPreferencesStore: DataStore<AppPreferences> by dataStore(
  fileName = DATA_STORE_FILE_NAME,
  serializer = AppPreferencesSerializer,
  produceMigrations = { context ->
    listOf(SharedPreferencesMigration(
      context = context,
      sharedPreferencesName = PREFERENCES_NAME,
      keysToMigrate = setOf(KEY_THEME, KEY_OEM_WARNING_DIALOG)
    ) { sharedPrefs: SharedPreferencesView, currentData: AppPreferences ->
      var newCurrentData = currentData

      if (newCurrentData.theme == AppPreferences.Theme.UNSPECIFIED) {
        newCurrentData = currentData.toBuilder()
          .setTheme(getThemeForStorageValue(context, sharedPrefs))
          .build()
      }

      val preferencesOemWarningDialog = sharedPrefs.getBoolean(KEY_OEM_WARNING_DIALOG, false)
      if (newCurrentData.oemWarningDialog != preferencesOemWarningDialog) {
        newCurrentData = newCurrentData.toBuilder()
          .setOemWarningDialog(preferencesOemWarningDialog)
          .build()
      }

      newCurrentData
    })
  }
)

private fun getThemeForStorageValue(context: Context, sharedPrefs: SharedPreferencesView): AppPreferences.Theme {
  val defaultThemeValue = context.getString(R.string.pref_theme_auto)

  return when (sharedPrefs.getString(KEY_THEME, defaultThemeValue)) {
    context.getString(R.string.pref_theme_dark) -> AppPreferences.Theme.DARK
    context.getString(R.string.pref_theme_light) -> AppPreferences.Theme.LIGHT
    else -> AppPreferences.Theme.AUTO
  }
}
