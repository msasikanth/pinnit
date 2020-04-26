package dev.sasikanth.pinnit.editor

import com.spotify.mobius.First
import com.spotify.mobius.First.first
import com.spotify.mobius.Init

class EditorScreenInit : Init<EditorScreenModel, EditorScreenEffect> {
  override fun init(model: EditorScreenModel): First<EditorScreenModel, EditorScreenEffect> {
    return if (model.notificationUuid != null) {
      first(model, setOf(LoadNotification(model.notificationUuid)))
    } else {
      first(model)
    }
  }
}
