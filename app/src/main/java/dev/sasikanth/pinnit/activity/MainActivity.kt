package dev.sasikanth.pinnit.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.setEdgeToEdgeSystemUiFlags
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.data.Schedule
import dev.sasikanth.pinnit.data.ScheduleType
import dev.sasikanth.pinnit.data.preferences.AppPreferences
import dev.sasikanth.pinnit.databinding.ActivityMainBinding
import dev.sasikanth.pinnit.notifications.NotificationCard
import dev.sasikanth.pinnit.oemwarning.OemWarningDialog
import dev.sasikanth.pinnit.oemwarning.shouldShowWarningForOEM
import dev.sasikanth.pinnit.theme.PinnitTheme
import dev.sasikanth.pinnit.utils.DispatcherProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var appPreferencesStore: DataStore<AppPreferences>

  @Inject
  lateinit var dispatcherProvider: DispatcherProvider

  private var navController: NavController? = null

  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.mainRoot.setEdgeToEdgeSystemUiFlags()

    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
    navController = navHostFragment.navController

    lifecycleScope.launchWhenResumed {
      val isOemWarningDialogShown = withContext(dispatcherProvider.io) {
        appPreferencesStore.data.first().oemWarningDialog
      }
      showOemWarningDialog(isOemWarningDialogShown)
    }
  }

  private suspend fun showOemWarningDialog(isOemWarningDialogShown: Boolean) {
    val brandName = Build.BRAND.lowercase(Locale.getDefault())
    if (isOemWarningDialogShown.not() && shouldShowWarningForOEM(brandName)) {
      appPreferencesStore.updateData { currentData ->
        currentData.toBuilder()
          .setOemWarningDialog(true)
          .build()
      }

      OemWarningDialog.show(supportFragmentManager)
    }
  }

  override fun onDestroy() {
    navController = null
    super.onDestroy()
  }
}
