package dev.sasikanth.pinnit.editor

import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update
import dev.sasikanth.pinnit.data.PinnitNotification

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

      is AddScheduleClicked -> addSchedule(event, model)

      RemoveScheduleClicked -> next(model.removeSchedule())

      ScheduleRepeatUnchecked -> next(model.removeScheduleRepeat())

      ScheduleRepeatChecked -> next(model.addScheduleRepeat())

      ScheduleDateClicked -> dispatch(setOf(ShowDatePicker(model.schedule!!.scheduleDate!!)))

      ScheduleTimeClicked -> dispatch(setOf(ShowTimePicker(model.schedule!!.scheduleTime!!)))

      is ScheduleDateChanged -> next(
        model.scheduleDateChanged(event.date),
        setOf(ValidateSchedule(scheduleDate = event.date, scheduleTime = model.schedule!!.scheduleTime!!))
      )

      is ScheduleTimeChanged -> next(
        model.scheduleTimeChanged(event.time),
        setOf(ValidateSchedule(scheduleDate = model.schedule!!.scheduleDate!!, scheduleTime = event.time))
      )

      is ScheduleTypeChanged -> next(model.scheduleTypeChanged(event.scheduleType))

      is NotificationSaved -> notificationSaved(event.notification)

      is NotificationUpdated -> notificationUpdated(model, event.updatedNotification)

      is ScheduleValidated -> next(model.scheduleValidated(event.result))
    }
  }

  private fun addSchedule(event: AddScheduleClicked, model: EditorScreenModel): Next<EditorScreenModel, EditorScreenEffect> {
    return next(model.addSchedule(schedule = event.schedule))
  }

  private fun notificationLoaded(model: EditorScreenModel, event: NotificationLoaded): Next<EditorScreenModel, EditorScreenEffect> {
    val updatedModel = model
      .notificationLoaded(event.notification)
      .titleChanged(event.notification.title)
      .contentChanged(event.notification.content)
      .scheduleLoaded(event.notification.schedule)

    val effects = mutableSetOf<EditorScreenEffect>(
      SetTitleAndContent(event.notification.title, event.notification.content)
    )

    if (event.notification.hasSchedule) {
      val schedule = event.notification.schedule!!
      effects.add(
        ValidateSchedule(scheduleDate = schedule.scheduleDate!!, scheduleTime = schedule.scheduleTime!!)
      )
    }

    return next(updatedModel, effects)
  }

  private fun saveClicked(model: EditorScreenModel): Next<EditorScreenModel, EditorScreenEffect> {
    val effect = if (model.notification == null) {
      val canPinNotification = model.schedule == null

      SaveNotification(
        title = model.title!!,
        content = model.content,
        schedule = model.schedule,
        canPinNotification = canPinNotification
      )
    } else {
      UpdateNotification(
        notificationUuid = model.notification.uuid,
        title = model.title!!,
        content = model.content,
        schedule = model.schedule
      )
    }

    return dispatch(setOf(effect))
  }

  private fun backClicked(model: EditorScreenModel): Next<EditorScreenModel, EditorScreenEffect> {
    if (model.hasTitleAndContentChanged || model.hasScheduleChanged) {
      return dispatch(setOf(ShowConfirmExitEditor))
    }

    return dispatch(setOf(CloseEditor))
  }

  private fun notificationSaved(notification: PinnitNotification): Next<EditorScreenModel, EditorScreenEffect> {
    val effects = mutableSetOf<EditorScreenEffect>()

    if (notification.isPinned) {
      effects.add(ShowNotification(notification))
    }

    if (notification.hasSchedule) {
      effects.add(ScheduleNotification(notification))
    }

    // We need to close the editor once all the pre-requisites are finished.
    // like showing notification or scheduling.
    effects.add(CloseEditor)

    return dispatch(effects)
  }

  private fun notificationUpdated(model: EditorScreenModel, updatedNotification: PinnitNotification): Next<EditorScreenModel, EditorScreenEffect> {
    val effects = mutableSetOf<EditorScreenEffect>()

    if (updatedNotification.isPinned) {
      effects.add(ShowNotification(updatedNotification))
    }

    when {
      updatedNotification.hasSchedule -> {
        effects.add(ScheduleNotification(updatedNotification))
      }
      // When we are updating a notification and schedule is removed
      // then we need to cancel the schedule.
      updatedNotification.hasSchedule.not() && model.notification?.hasSchedule == true -> {
        effects.add(CancelNotificationSchedule(updatedNotification.uuid))
      }
    }

    // We need to close the editor once all the pre-requisites are finished.
    // like showing notification or scheduling.
    effects.add(CloseEditor)

    return dispatch(effects)
  }
}
