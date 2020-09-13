package dev.sasikanth.pinnit.editor

import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update

class EditorScreenUpdate : Update<EditorScreenModel, EditorScreenEvent, EditorScreenEffect> {
  override fun update(model: EditorScreenModel, event: EditorScreenEvent): Next<EditorScreenModel, EditorScreenEffect> {
    return when (event) {
      is NotificationLoaded -> notificationLoaded(model, event)

      is TitleChanged -> next(model.titleChanged(event.title))

      is ContentChanged -> next(model.contentChanged(event.content))

      is SaveClicked -> saveClicked(model)

      BackClicked -> backClicked(model)

      ConfirmedExit -> dispatch(setOf(CloseEditor))

      DeleteNotificationClicked -> dispatch(setOf(ShowConfirmDelete))

      ConfirmDeleteNotification -> dispatch(setOf(DeleteNotification(model.notification!!)))
    }
  }

  private fun notificationLoaded(model: EditorScreenModel, event: NotificationLoaded): Next<EditorScreenModel, EditorScreenEffect> {
    val updatedModel = model
      .notificationLoaded(event.notification)
      .titleChanged(event.notification.title)
      .contentChanged(event.notification.content)
    return next(updatedModel, setOf(SetTitleAndContent(event.notification.title, event.notification.content)))
  }

  private fun saveClicked(model: EditorScreenModel): Next<EditorScreenModel, EditorScreenEffect> {
    val effect = if (model.notification == null) {
      SaveNotificationAndCloseEditor(model.title!!, model.content)
    } else {
      UpdateNotificationAndCloseEditor(model.notification.uuid, model.title!!, model.content)
    }

    return dispatch(setOf(effect))
  }

  private fun backClicked(model: EditorScreenModel): Next<EditorScreenModel, EditorScreenEffect> {
    if (model.hasTitleAndContentChanged) {
      return dispatch(setOf(ShowConfirmExitEditor))
    }

    return dispatch(setOf(CloseEditor))
  }
}
