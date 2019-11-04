package dev.sasikanth.pinnit.pages.apps

import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sasikanth.pinnit.data.AppItem
import dev.sasikanth.pinnit.utils.PinnitPreferences
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppsViewModel
@Inject constructor(
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
            val installedApps = withContext(Dispatchers.Default) {
                val mainIntent = Intent(Intent.ACTION_MAIN, null)
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                packageManager.queryIntentActivities(mainIntent, PackageManager.GET_META_DATA)
                    .map {
                        val ci = it.activityInfo
                        AppItem(
                            packageName = ci.packageName,
                            appName = it.loadLabel(packageManager).toString(),
                            icon = it.loadIcon(packageManager),
                            isSelected = pinnitPreferences.allowedApps.contains(ci.packageName)
                        )
                    }.sortedBy { it.appName.toLowerCase(Locale.getDefault()) }
            }
            _installedApps.value = installedApps
        }
    }

    fun setAllowState(appItem: AppItem) {
        val allowedApps = pinnitPreferences.allowedApps
        pinnitPreferences.allowedApps = allowedApps.apply {
            if (allowedApps.contains(appItem.packageName)) {
                allowedApps.remove(appItem.packageName)
            } else {
                allowedApps.add(appItem.packageName)
            }
        }
        loadInstalledApps()
    }
}
