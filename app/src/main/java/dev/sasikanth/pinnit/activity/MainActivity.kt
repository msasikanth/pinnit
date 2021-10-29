package dev.sasikanth.pinnit.activity

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import dev.chrisbanes.insetter.setEdgeToEdgeSystemUiFlags
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.preferences.AppPreferences
import dev.sasikanth.pinnit.databinding.ActivityMainBinding
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.editor.EditorScreenArgs
import dev.sasikanth.pinnit.oemwarning.OemWarningDialog
import dev.sasikanth.pinnit.oemwarning.shouldShowWarningForOEM
import dev.sasikanth.pinnit.utils.DispatcherProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var appPreferencesStore: DataStore<AppPreferences>

  @Inject
  lateinit var dispatcherProvider: DispatcherProvider

  private var navController: NavController? = null
  private val onNavDestinationChangeListener = NavController.OnDestinationChangedListener { _, destination, arguments ->
    binding.appBarLayout.setExpanded(true, true)
    when (destination.id) {
      R.id.notificationsScreen -> {
        binding.toolbarTitleTextView.text = getString(R.string.toolbar_title_notifications)
      }
      R.id.editorScreen -> {
        if (arguments != null) {
          // If there is a notification present
          // we will be showing the edit title or else we show
          // create title
          val editorScreenArgs = EditorScreenArgs.fromBundle(arguments)
          val toolbarTitle = if (editorScreenArgs.notificationUuid == null) {
            getString(R.string.toolbar_title_create)
          } else {
            getString(R.string.toolbar_title_edit)
          }
          binding.toolbarTitleTextView.text = toolbarTitle
        }
      }
    }
  }

  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    injector.inject(this)
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.mainRoot.setEdgeToEdgeSystemUiFlags()
    binding.toolbar.apply {
      applySystemWindowInsetsToPadding(top = true, right = true, left = true)
      setSupportActionBar(this)
    }

    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
    navController = navHostFragment.navController
    navController?.addOnDestinationChangedListener(onNavDestinationChangeListener)

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
    navController?.removeOnDestinationChangedListener(onNavDestinationChangeListener)
    navController = null
    super.onDestroy()
  }
}
