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
      // During create we will have title and content as null
      // to prevent setting an empty title and content during restore
      // and overriding any text that is entered. We check if both title and content
      // are null before setting the empty title and content
      if (model.title == null && model.content == null) {
        return first(model, setOf(SetEmptyTitleAndContent))
      }
    }

    return first(model)
  }
}
