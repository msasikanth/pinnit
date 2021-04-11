package dev.sasikanth.pinnit.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore

private const val DATA_STORE_FILE_NAME = "app_prefs.pb"

val Context.appPreferencesStore: DataStore<AppPreferences> by dataStore(
  fileName = DATA_STORE_FILE_NAME,
  serializer = AppPreferencesSerializer
)
