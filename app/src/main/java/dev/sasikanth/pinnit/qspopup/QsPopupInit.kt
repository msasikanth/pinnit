package dev.sasikanth.pinnit.qspopup

import com.spotify.mobius.First
import com.spotify.mobius.First.first
import com.spotify.mobius.Init

class QsPopupInit : Init<QsPopupModel, QsPopupEffect> {
  override fun init(model: QsPopupModel): First<QsPopupModel, QsPopupEffect> {
    return first(model, setOf(LoadNotifications))
  }
}
