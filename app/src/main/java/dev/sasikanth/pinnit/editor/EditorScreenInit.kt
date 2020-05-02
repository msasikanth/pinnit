package dev.sasikanth.pinnit.editor

import com.spotify.mobius.First
import com.spotify.mobius.First.first
import com.spotify.mobius.Init

class EditorScreenInit : Init<EditorScreenModel, EditorScreenEffect> {
  override fun init(model: EditorScreenModel): First<EditorScreenModel, EditorScreenEffect> {
    if (model.title == null) {
      if (model.notificationUuid != null) {
        return first(model, setOf(LoadNotification(model.notificationUuid)))
      }
    }

    return first(model)
  }
}
