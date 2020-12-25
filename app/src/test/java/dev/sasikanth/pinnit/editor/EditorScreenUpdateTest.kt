package dev.sasikanth.pinnit.editor

import com.spotify.mobius.test.NextMatchers.hasEffects
import com.spotify.mobius.test.NextMatchers.hasModel
import com.spotify.mobius.test.NextMatchers.hasNoEffects
import com.spotify.mobius.test.NextMatchers.hasNoModel
import com.spotify.mobius.test.UpdateSpec
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import dev.sasikanth.pinnit.TestData
import dev.sasikanth.pinnit.data.ScheduleType
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class EditorScreenUpdateTest {

  private val updateSpec = UpdateSpec(EditorScreenUpdate())
  private val notificationUuid = UUID.fromString("87722f77-865a-4df3-8c82-9d1c7b3fd5bb")
  private val notification = TestData.notification(
    uuid = notificationUuid
  )
  private val defaultModel = EditorScreenModel.default(notificationUuid, null, null)

  @Test
  fun `when notification is loaded, then update the ui`() {
    updateSpec
      .given(defaultModel)
      .whenEvent(NotificationLoaded(notification))
      .then(
        assertThatNext(
          hasModel(
            defaultModel
              .notificationLoaded(notification)
              .titleChanged(notification.title)
              .contentChanged(notification.content)
              .scheduleLoaded(notification.schedule)
          ),
          hasEffects(SetTitleAndContent(notification.title, notification.content) as EditorScreenEffect)
        )
      )
  }

  @Test
  fun `when title changes and is not empty, then update ui`() {
    val title = "Title"

    updateSpec
      .given(defaultModel)
      .whenEvent(TitleChanged(title))
      .then(
        assertThatNext(
          hasModel(defaultModel.titleChanged(title)),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when content changes, then update ui`() {
    val content = "Content"

    updateSpec
      .given(defaultModel)
      .whenEvent(ContentChanged(content))
      .then(
        assertThatNext(
          hasModel(defaultModel.contentChanged(content)),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when save is clicked and notification uuid is not present, then save the notification`() {
    val model = EditorScreenModel
      .default(null, null, null)
      .titleChanged("Title")
      .contentChanged("Content")

    updateSpec
      .given(model)
      .whenEvent(SaveClicked)
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(SaveNotification(model.title!!, model.content, model.schedule) as EditorScreenEffect)
        )
      )
  }

  @Test
  fun `when save is clicked and notification is present, then update the notification`() {
    val pinnedNotificationUuid = UUID.fromString("ad9ca7be-55a6-492c-bd5a-4e6d5860e973")
    val pinnedNotification = TestData.notification(
      uuid = pinnedNotificationUuid,
      isPinned = true
    )
    val model = EditorScreenModel.default(pinnedNotificationUuid, null, null)
      .notificationLoaded(pinnedNotification)
      .titleChanged("Title")
      .contentChanged("Content")

    updateSpec
      .given(model)
      .whenEvent(SaveClicked)
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(
            UpdateNotification(
              notificationUuid = pinnedNotificationUuid,
              title = model.title!!,
              content = model.content,
              schedule = model.schedule
            ) as EditorScreenEffect
          )
        )
      )
  }

  @Test
  fun `when back is clicked and title and content are not changed, then close editor`() {
    val notificationTitle = "Title"
    val notificationContent = "Content"
    val notification = TestData.notification(
      uuid = UUID.fromString("33605259-a4b2-4fc7-b4a6-90cf75215777"),
      title = notificationTitle,
      content = notificationContent
    )
    val model = EditorScreenModel.default(notificationUuid, null, null)
      .notificationLoaded(notification)
      .titleChanged(notificationTitle)
      .contentChanged(notificationContent)

    updateSpec
      .given(model)
      .whenEvent(BackClicked)
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(CloseEditor as EditorScreenEffect)
        )
      )
  }

  @Test
  fun `when back is clicked and title and content are changed, then show confirm exit editor`() {
    updateSpec
      .given(defaultModel)
      .whenEvents(
        TitleChanged("Updated Title"),
        BackClicked
      )
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(ShowConfirmExitEditor as EditorScreenEffect)
        )
      )
  }

  @Test
  fun `when confirmed exit is clicked, then close editor`() {
    updateSpec
      .given(defaultModel)
      .whenEvents(
        TitleChanged("Updated Title"),
        BackClicked,
        ConfirmedExit
      )
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(CloseEditor as EditorScreenEffect)
        )
      )
  }

  @Test
  fun `when delete notification is clicked, then show confirm delete dialog`() {
    val model = defaultModel
      .notificationLoaded(notification)
      .titleChanged(notification.title)
      .contentChanged(notification.content)

    updateSpec
      .given(model)
      .whenEvent(DeleteNotificationClicked)
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(ShowConfirmDelete as EditorScreenEffect)
        )
      )
  }

  @Test
  fun `when confirm delete notification is clicked, then delete the notification`() {
    val model = defaultModel
      .notificationLoaded(notification)
      .titleChanged(notification.title)
      .contentChanged(notification.content)

    updateSpec
      .given(model)
      .whenEvent(ConfirmDeleteNotification)
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(DeleteNotification(notification) as EditorScreenEffect)
        )
      )
  }

  @Test
  fun `when add schedule button is clicked, then add schedule`() {
    val schedule = TestData.schedule()

    updateSpec
      .given(defaultModel)
      .whenEvent(AddScheduleClicked(schedule))
      .then(
        assertThatNext(
          hasModel(defaultModel.addSchedule(schedule)),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when remove schedule button is clicked, then remove schedule`() {
    val schedule = TestData.schedule()
    val scheduleLoadedModel = defaultModel.scheduleLoaded(schedule)

    updateSpec
      .given(scheduleLoadedModel)
      .whenEvent(RemoveScheduleClicked)
      .then(
        assertThatNext(
          hasModel(scheduleLoadedModel.removeSchedule()),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when schedule repeat is unchecked, then remove the schedule repeat type`() {
    val schedule = TestData.schedule(scheduleType = ScheduleType.Daily)
    val scheduleLoadedModel = defaultModel.scheduleLoaded(schedule)

    updateSpec
      .given(scheduleLoadedModel)
      .whenEvent(ScheduleRepeatUnchecked)
      .then(
        assertThatNext(
          hasModel(scheduleLoadedModel.removeScheduleRepeat()),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when schedule repeat is checked, then add the schedule repeat type`() {
    val schedule = TestData.schedule(scheduleType = null)
    val scheduleLoadedModel = defaultModel.scheduleLoaded(schedule)

    updateSpec
      .given(scheduleLoadedModel)
      .whenEvent(ScheduleRepeatChecked)
      .then(
        assertThatNext(
          hasModel(scheduleLoadedModel.addScheduleRepeat()),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when schedule date is clicked, then show date picker`() {
    val scheduleDate = LocalDate.parse("2020-01-01")
    val schedule = TestData.schedule(scheduleDate = scheduleDate)
    val scheduleLoadedModel = defaultModel.scheduleLoaded(schedule)

    updateSpec
      .given(scheduleLoadedModel)
      .whenEvent(ScheduleDateClicked)
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(ShowDatePicker(scheduleDate))
        )
      )
  }

  @Test
  fun `when schedule time is clicked, then show time picker`() {
    val scheduleTime = LocalTime.parse("09:00:00")
    val schedule = TestData.schedule(scheduleTime = scheduleTime)
    val scheduleLoadedModel = defaultModel.scheduleLoaded(schedule)

    updateSpec
      .given(scheduleLoadedModel)
      .whenEvent(ScheduleTimeClicked)
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(ShowTimePicker(scheduleTime))
        )
      )
  }

  @Test
  fun `when schedule date is changed, then update the model`() {
    val schedule = TestData.schedule(scheduleDate = LocalDate.parse("2020-01-01"))
    val scheduleLoadedModel = defaultModel.scheduleLoaded(schedule)
    val updatedLocalDate = LocalDate.parse("2020-01-05")

    updateSpec
      .given(scheduleLoadedModel)
      .whenEvent(ScheduleDateChanged(updatedLocalDate))
      .then(
        assertThatNext(
          hasModel(scheduleLoadedModel.scheduleDateChanged(updatedLocalDate)),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when schedule time is changed, then update the model`() {
    val schedule = TestData.schedule(scheduleTime = LocalTime.parse("09:00:00"))
    val scheduleLoadedModel = defaultModel.scheduleLoaded(schedule)
    val updatedLocalTime = LocalTime.parse("09:00:00")

    updateSpec
      .given(scheduleLoadedModel)
      .whenEvent(ScheduleTimeChanged(updatedLocalTime))
      .then(
        assertThatNext(
          hasModel(scheduleLoadedModel.scheduleTimeChanged(updatedLocalTime)),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when schedule type is changed, then update the model`() {
    val schedule = TestData.schedule(scheduleType = ScheduleType.Daily)
    val scheduleLoadedModel = defaultModel.scheduleLoaded(schedule)

    updateSpec
      .given(scheduleLoadedModel)
      .whenEvent(ScheduleTypeChanged(ScheduleType.Monthly))
      .then(
        assertThatNext(
          hasModel(scheduleLoadedModel.scheduleTypeChanged(ScheduleType.Monthly)),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when notification is saved, then show notification if it's pinned and close editor`() {
    val notification = TestData.notification(
      title = "Sample Title",
      content = "Sample Content",
      isPinned = true
    )
    val notificationSavedModel = defaultModel
      .titleChanged("Sample Title")
      .contentChanged("Sample Content")

    updateSpec
      .given(notificationSavedModel)
      .whenEvent(NotificationSaved(notification))
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(ShowNotification(notification), CloseEditor)
        )
      )
  }

  @Test
  fun `when notification is saved, then close editor if the notification is not pinned`() {
    val notification = TestData.notification(
      title = "Sample Title",
      content = "Sample Content",
      isPinned = false
    )
    val notificationSavedModel = defaultModel
      .titleChanged("Sample Title")
      .contentChanged("Sample Content")

    updateSpec
      .given(notificationSavedModel)
      .whenEvent(NotificationSaved(notification))
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(CloseEditor)
        )
      )
  }
}
