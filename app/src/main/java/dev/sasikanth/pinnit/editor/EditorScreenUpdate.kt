package dev.sasikanth.pinnit.editor

import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update

class EditorScreenUpdate : Update<EditorScreenModel, EditorScreenEvent, EditorScreenEffect> {
  override fun update(model: EditorScreenModel, event: EditorScreenEvent): Next<EditorScreenModel, EditorScreenEffect> {
    return when (event) {
      is NotificationLoaded -> {
        val updatedModel = model.titleChanged(event.notification.title)
          .contentChanged(event.notification.content)
        next(updatedModel)
      }

      is TitleChanged -> {
        next(model.titleChanged(event.title))
      }

      is SaveClicked -> {
        val effect = if (model.notificationUuid == null) {
          SaveNotificationAndCloseEditor(model.title!!, model.content)
        } else {
          UpdateNotificationAndCloseEditor(model.notificationUuid, model.title!!, model.content)
        }
        return dispatch(setOf(effect))
      }
    }
  }
}
