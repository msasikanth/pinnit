package dev.sasikanth.pinnit.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.findNavController
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.utils.donOnApplyWindowInsets
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

  private var navController: NavController? = null
  private val onNavDestinationChangeListener = NavController.OnDestinationChangedListener { _, destination, _ ->
    if (destination.id == R.id.notificationsFragment) {
      toolbarTitleTextView.text = getString(R.string.toolbar_title_notifications)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    mainRoot.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

    appBarLayout.donOnApplyWindowInsets { view, windowInsets, initialPadding ->
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
