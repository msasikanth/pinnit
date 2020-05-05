package dev.sasikanth.pinnit

import android.content.Intent.EXTRA_TEXT
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDeepLinkBuilder
import dev.sasikanth.pinnit.activity.MainActivity
import dev.sasikanth.pinnit.editor.EditorScreenArgs

class ShortcutReceiverActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val receivedText = intent.getStringExtra(EXTRA_TEXT)

    // Launch the deep link to editor page
    NavDeepLinkBuilder(this)
      .setComponentName(MainActivity::class.java)
      .setDestination(R.id.editorScreen)
      .setGraph(R.navigation.main_nav_graph)
      .setArguments(EditorScreenArgs(notificationContent = receivedText).toBundle())
      .createTaskStackBuilder()
      .startActivities()

    // finish the activity once necessary actions are performed
    finish()
  }
}
