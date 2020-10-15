package dev.sasikanth.pinnit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDeepLinkBuilder
import dev.sasikanth.pinnit.activity.MainActivity
import dev.sasikanth.pinnit.editor.EditorScreenArgs
import dev.sasikanth.pinnit.editor.EditorTransition

class AppActionsReceiverActivity : AppCompatActivity() {

  companion object {
    const val MESSAGE = "message"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val receivedText = intent?.data?.getQueryParameter(MESSAGE)
    val editorScreenArgs = EditorScreenArgs(
      notificationTitle = receivedText,
      editorTransition = EditorTransition.SharedAxis
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
