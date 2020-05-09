package dev.sasikanth.pinnit.qspopup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDeepLinkBuilder
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.activity.MainActivity
import dev.sasikanth.pinnit.editor.EditorScreenArgs
import kotlinx.android.synthetic.main.activity_qs_popup.*

/**
 * This activity will act as "dialog" to be opened when QS tile is
 * clicked. The reason why I didn't use an actual dialog is because it's not
 * properly following the app theme (day/night).
 */
class QsPopupActivity : AppCompatActivity(R.layout.activity_qs_popup) {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    backgroundRoot.setOnClickListener { finish() }

    openAppButton.setOnClickListener {
      Intent(this, MainActivity::class.java).also {
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(it)
      }
    }

    createNotificationButton.setOnClickListener {
      NavDeepLinkBuilder(this)
        .setComponentName(MainActivity::class.java)
        .setGraph(R.navigation.main_nav_graph)
        .setDestination(R.id.editorScreen)
        .setArguments(EditorScreenArgs().toBundle())
        .createTaskStackBuilder()
        .startActivities()
    }
  }
}
