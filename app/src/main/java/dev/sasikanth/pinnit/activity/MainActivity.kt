package dev.sasikanth.pinnit.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import dev.chrisbanes.insetter.setEdgeToEdgeSystemUiFlags
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.editor.EditorScreenArgs
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

  private var navController: NavController? = null
  private val onNavDestinationChangeListener = NavController.OnDestinationChangedListener { _, destination, arguments ->
    appBarLayout.setExpanded(true, true)
    when (destination.id) {
      R.id.notificationsScreen -> {
        toolbarTitleTextView.text = getString(R.string.toolbar_title_notifications)
      }
      R.id.editorScreen -> {
        if (arguments != null) {
          // If there is a notification present
          // we will be showing the edit title or else we show
          // create title
          val editorScreenArgs = EditorScreenArgs.fromBundle(arguments)
          if (editorScreenArgs.notificationUuid == null) {
            toolbarTitleTextView.text = getString(R.string.toolbar_title_create)
          } else {
            toolbarTitleTextView.text = getString(R.string.toolbar_title_edit)
          }
        }
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    injector.inject(this)
    super.onCreate(savedInstanceState)

    mainRoot.setEdgeToEdgeSystemUiFlags()
    toolbar.applySystemWindowInsetsToPadding(top = true, right = true, left = true)
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
