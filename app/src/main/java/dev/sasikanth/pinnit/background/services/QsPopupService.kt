package dev.sasikanth.pinnit.background.services

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile.STATE_INACTIVE
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import dev.sasikanth.pinnit.qspopup.QsPopupActivity

@RequiresApi(Build.VERSION_CODES.N)
class QsPopupService : TileService() {

  override fun onClick() {
    val intent = Intent(this, QsPopupActivity::class.java).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivityAndCollapse(intent)
  }

  override fun onTileAdded() {
    super.onTileAdded()
    updateQsTileState()
  }

  override fun onStartListening() {
    super.onStartListening()
    updateQsTileState()
  }

  private fun updateQsTileState() {
    qsTile.state = STATE_INACTIVE
    qsTile.updateTile()
  }
}
