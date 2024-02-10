package dev.sasikanth.pinnit.background.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import dev.sasikanth.pinnit.qspopup.QsPopupActivity

@RequiresApi(Build.VERSION_CODES.N)
class QsPopupService : TileService() {

  @Suppress("DEPRECATION")
  @SuppressLint("StartActivityAndCollapseDeprecated")
  override fun onClick() {
    val intent = Intent(this, QsPopupActivity::class.java).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      startActivityAndCollapse(pi)
    } else {
      startActivityAndCollapse(intent)
    }
  }
}
