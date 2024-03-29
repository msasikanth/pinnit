package dev.sasikanth.pinnit.editor

import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import dev.sasikanth.sharedtestcode.TestData
import dev.sasikanth.pinnit.data.ScheduleType
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class EditorScreenUiRenderTest {

  private val ui = mock<EditorScreenUi>()
  private val uiRender = EditorScreenUiRender(ui)

  private val notificationUuid = UUID.fromString("62f36ab9-9a54-481a-9db7-c856766975ce")

  @Test
  fun `when no notification is present, then update ui`() {
    // given
    val model = EditorScreenModel.default(null, null, null)
      .titleChanged(null)

    // when
    uiRender.render(model)

    // then
    verify(ui).renderSaveAndPinActionButtonText()
    verify(ui).hideDeleteButton()
    verify(ui).disableSave()
    verify(ui).hideScheduleView()
    verifyNoMoreInteractions(ui)
  }

  @Test
  fun `when notification is present, then update ui`() {
    // given
    val notification = dev.sasikanth.sharedtestcode.TestData.notification(
      uuid = notificationUuid,
      schedule = null
    )
    val model = EditorScreenModel.default(notificationUuid, null, null)
      .notificationLoaded(notification)
      .titleChanged(notification.title)
      .contentChanged(notification.content)

    // when
    uiRender.render(model)

    // then
    verify(ui).renderSaveActionButtonText()
    verify(ui).showDeleteButton()
    verify(ui).disableSave()
    verify(ui).hideScheduleView()
    verifyNoMoreInteractions(ui)
  }

  @Test
  fun `when notification is present and title is changed, then enable save`() {
    val notificationTitle = "Notification Title"

    val model = EditorScreenModel.default(notificationUuid, null, null)
      .notificationLoaded(
        dev.sasikanth.sharedtestcode.TestData.notification(
          uuid = notificationUuid,
          title = "Original Notification Title"
        )
      )
      .titleChanged(notificationTitle)

    // then
    uiRender.render(model)

    // then
    verify(ui).enableSave()
    verify(ui).showDeleteButton()
    verify(ui).renderSaveActionButtonText()
    verify(ui).hideScheduleView()
    verifyNoMoreInteractions(ui)
  }

  @Test
  fun `when title is blank, then disable save`() {
    val title = ""

    val model = EditorScreenModel.default(notificationUuid, null, null)
      .titleChanged(title)

    // then
    uiRender.render(model)

    // then
    verify(ui).disableSave()
    verify(ui).hideDeleteButton()
    verify(ui).renderSaveAndPinActionButtonText()
    verify(ui).hideScheduleView()
    verifyNoMoreInteractions(ui)
  }

  @Test
  fun `when schedule is present, then show schedule view`() {
    // given
    val scheduleDate = LocalDate.parse("2020-01-01")
    val scheduleTime = LocalTime.parse("09:00:00")
    val scheduleType = ScheduleType.Daily
    val schedule = dev.sasikanth.sharedtestcode.TestData.schedule(
      scheduleDate = scheduleDate,
      scheduleTime = scheduleTime,
      scheduleType = scheduleType
    )

    val model = EditorScreenModel.default(notificationUuid, null, null)
      .scheduleLoaded(schedule)

    // when
    uiRender.render(model)

    // then
    verify(ui).disableSave()
    verify(ui).hideDeleteButton()
    verify(ui).renderSaveActionButtonText()
    verify(ui).showScheduleView()
    verify(ui).renderScheduleDateTime(scheduleDate, scheduleTime)
    verify(ui).renderScheduleRepeat(scheduleType, hasValidScheduleResult = true)
    verify(ui).hideScheduleWarning()
    verifyNoMoreInteractions(ui)
  }

  @Test
  fun `when schedule is not present, then hide schedule view`() {
    // given
    val model = EditorScreenModel.default(notificationUuid, null, null)

    // when
    uiRender.render(model)

    // then
    verify(ui).disableSave()
    verify(ui).hideDeleteButton()
    verify(ui).renderSaveAndPinActionButtonText()
    verify(ui).hideScheduleView()
    verifyNoMoreInteractions(ui)
  }

  @Test
  fun `when schedule validation result is not present, then enable the save button`() {
    // given
    val scheduleDate = LocalDate.parse("2020-01-01")
    val scheduleTime = LocalTime.parse("09:00:00")
    val scheduleType = ScheduleType.Daily
    val schedule = dev.sasikanth.sharedtestcode.TestData.schedule(
      scheduleDate = scheduleDate,
      scheduleTime = scheduleTime,
      scheduleType = scheduleType
    )

    val model = EditorScreenModel.default(notificationUuid, "Sample", null)
      .scheduleLoaded(schedule)

    // when
    uiRender.render(model)

    // then
    verify(ui).enableSave()
    verify(ui).hideDeleteButton()
    verify(ui).renderSaveActionButtonText()
    verify(ui).showScheduleView()
    verify(ui).renderScheduleDateTime(scheduleDate, scheduleTime)
    verify(ui).renderScheduleRepeat(scheduleType, hasValidScheduleResult = true)
    verify(ui).hideScheduleWarning()
    verifyNoMoreInteractions(ui)
  }
}
