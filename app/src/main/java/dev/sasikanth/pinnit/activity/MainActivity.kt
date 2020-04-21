package dev.sasikanth.pinnit.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dev.sasikanth.pinnit.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    mainRoot.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
  }
}
