package dev.sasikanth.pinnit.pages.apps

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sasikanth.pinnit.utils.PinnitPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppsViewModel
@Inject constructor(
    private val context: Context,
    private val packageManager: PackageManager,
    private val pinnitPreferences: PinnitPreferences
) : ViewModel() {

  private val _installedApps = MutableLiveData<List<AppItem>>()
  val installedApps: LiveData<List<AppItem>>
    get() = _installedApps

  init {
    loadInstalledApps()
  }

  private fun loadInstalledApps() {
    viewModelScope.launch {
      val installedApps = withContext(Dispatchers.IO) {
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
          addCategory(Intent.CATEGORY_LAUNCHER)
        }

        // Query apps
        packageManager.queryIntentActivities(mainIntent, PackageManager.GET_META_DATA)
            .map { resolveInfo ->
              val packageName = resolveInfo.activityInfo.packageName
              val appName = resolveInfo.loadLabel(packageManager).toString()
              val appIcon = resolveInfo.loadIcon(packageManager)
              val isAppSelected = pinnitPreferences.allowedApps.contains(packageName)

              AppItem(packageName, appName, appIcon, isAppSelected)
            }
            .filter { it.packageName != context.packageName }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.appName })
            .sortedByDescending { it.isSelected }
      }
      _installedApps.value = installedApps
    }
  }

  fun toggleAppSelection(appItem: AppItem) {
    val allowedApps = pinnitPreferences.allowedApps
    pinnitPreferences.allowedApps = allowedApps.apply {
      if (contains(appItem.packageName)) {
        remove(appItem.packageName)
      } else {
        add(appItem.packageName)
      }
    }
    loadInstalledApps()
  }
}
