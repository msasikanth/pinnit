package dev.sasikanth.pinnit

import android.content.Intent.EXTRA_TEXT
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDeepLinkBuilder
import dev.sasikanth.pinnit.activity.MainActivity
import dev.sasikanth.pinnit.editor.EditorScreenArgs
import dev.sasikanth.pinnit.editor.EditorTransition.SharedAxis

class ShortcutReceiverActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val receivedText = intent.getStringExtra(EXTRA_TEXT)
    val editorScreenArgs = EditorScreenArgs(
      notificationContent = receivedText,
      editorTransition = SharedAxis
    ).toBundle()

    // Launch the deep link to editor page
    NavDeepLinkBuilder(this)
      .setComponentName(MainActivity::class.java)
      .setDestination(R.id.editorScreen)
      .setGraph(R.navigation.main_nav_graph)
      .setArguments(editorScreenArgs)
      .createTaskStackBuilder()
      .startActivities()

    // finish the activity once necessary actions are performed
    finish()
  }
}
