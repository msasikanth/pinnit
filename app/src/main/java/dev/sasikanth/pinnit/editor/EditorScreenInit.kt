package dev.sasikanth.pinnit.editor

import com.spotify.mobius.First
import com.spotify.mobius.First.first
import com.spotify.mobius.Init

class EditorScreenInit : Init<EditorScreenModel, EditorScreenEffect> {
  override fun init(model: EditorScreenModel): First<EditorScreenModel, EditorScreenEffect> {
    if (model.notificationUuid != null) {
      // We are only loading notification if it's not already been loaded
      if (model.notification == null) {
        return first(model, setOf(LoadNotification(model.notificationUuid)))
      }
    } else {
      return first(model, setOf(SetTitleAndContent(model.title, model.content)))
    }

    return first(model)
  }
}
