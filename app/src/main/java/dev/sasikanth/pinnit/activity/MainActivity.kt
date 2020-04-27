package dev.sasikanth.pinnit.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.findNavController
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.PinnitPreferences
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.utils.donOnApplyWindowInsets
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(R.layout.activity_main) {

  // Injecting this to trigger the init
  // function of PinnitPreferences to update theme
  @Inject
  lateinit var pinnitPreferences: PinnitPreferences

  private var navController: NavController? = null
  private val onNavDestinationChangeListener = NavController.OnDestinationChangedListener { _, destination, arguments ->
    when (destination.id) {
      R.id.notificationsScreen -> {
        toolbarTitleTextView.text = getString(R.string.toolbar_title_notifications)
      }
      R.id.editorScreen -> {
        // If there is a notification uuid is present
        // we will be showing the edit title or else we show
        // create title
        val notificationUuid = arguments?.getString("uuid")
        if (notificationUuid == null) {
          toolbarTitleTextView.text = getString(R.string.toolbar_title_create)
        } else {
          toolbarTitleTextView.text = getString(R.string.toolbar_title_edit)
        }
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    injector.inject(this)
    super.onCreate(savedInstanceState)

    mainRoot.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

    toolbar.donOnApplyWindowInsets { view, windowInsets, initialPadding ->
      view.updatePadding(top = windowInsets.systemWindowInsetTop + initialPadding.top)
    }
    setSupportActionBar(toolbar)

    navController = findNavController(R.id.nav_host_fragment_container)
    navController?.addOnDestinationChangedListener(onNavDestinationChangeListener)
  }

  override fun onDestroy() {
    navController?.removeOnDestinationChangedListener(onNavDestinationChangeListener)
    navController = null
    super.onDestroy()
  }
}
