package dev.sasikanth.pinnit.qspopup

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.NavDeepLinkBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.activity.MainActivity
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.databinding.ActivityQsPopupBinding
import dev.sasikanth.pinnit.di.DateTimeFormat
import dev.sasikanth.pinnit.editor.EditorScreenArgs
import dev.sasikanth.pinnit.editor.EditorTransition.SharedAxis
import dev.sasikanth.pinnit.notifications.adapter.NotificationPinItemAnimator
import dev.sasikanth.pinnit.notifications.adapter.NotificationsListAdapter
import dev.sasikanth.pinnit.utils.UserClock
import dev.sasikanth.pinnit.utils.UtcClock
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * This activity will act as "dialog" to be opened when QS tile is
 * clicked. The reason why I didn't use an actual dialog is because it's not
 * properly following the app theme (day/night).
 */
@AndroidEntryPoint
class QsPopupActivity : AppCompatActivity(), QsPopupUi {

  @Inject
  lateinit var utcClock: UtcClock

  @Inject
  lateinit var userClock: UserClock

  @Inject
  @DateTimeFormat(DateTimeFormat.Type.ScheduleDateFormat)
  lateinit var scheduleDateFormatter: DateTimeFormatter

  @Inject
  @DateTimeFormat(DateTimeFormat.Type.ScheduleTimeFormat)
  lateinit var scheduleTimeFormatter: DateTimeFormatter

  private val viewModel: QsPopupViewModel by viewModels()

  private val uiRender = QsPopupUiRenderer(this)

  private lateinit var adapter: NotificationsListAdapter
  private lateinit var binding: ActivityQsPopupBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityQsPopupBinding.inflate(layoutInflater)
    setContentView(binding.root)

    adapter = NotificationsListAdapter(
      utcClock = utcClock,
      userClock = userClock,
      scheduleDateFormatter = scheduleDateFormatter,
      scheduleTimeFormatter = scheduleTimeFormatter,
      onToggleNotificationPinClicked = ::onToggleNotificationPinClicked,
      onNotificationClicked = { _, notification ->
        onNotificationClicked(notification)
      },
      onEditNotificationScheduleClicked = ::onEditNotificationScheduleClicked,
      onRemoveNotificationScheduleClicked = ::onRemoveNotificationScheduleClicked
    )
    binding.qsPopupNotificationsRecyclerView.adapter = adapter
    binding.qsPopupNotificationsRecyclerView.itemAnimator = NotificationPinItemAnimator()

    viewModel.models.observe(this) { model ->
      uiRender.render(model)
    }

    viewModel.viewEffects.setObserver(this) { viewEffect ->
      when (viewEffect) {
        is OpenNotificationEditorViewEffect -> openNotificationEditorView(
          notification = viewEffect.notification
        )
      }
    }

    binding.backgroundRoot.setOnClickListener { finish() }

    binding.openAppButton.setOnClickListener {
      Intent(this, MainActivity::class.java).also {
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(it)
      }
    }

    binding.createNotificationButton.setOnClickListener {
      openNotificationEditorView(notification = null)
    }
  }

  private fun openNotificationEditorView(
    notification: PinnitNotification?
  ) {
    NavDeepLinkBuilder(this)
      .setComponentName(MainActivity::class.java)
      .setGraph(R.navigation.main_nav_graph)
      .setDestination(R.id.editorScreen)
      .setArguments(
        EditorScreenArgs(
          notificationUuid = notification?.uuid,
          editorTransition = SharedAxis
        ).toBundle()
      )
      .createTaskStackBuilder()
      .startActivities()
  }

  override fun showNotifications(notifications: List<PinnitNotification>) {
    adapter.submitList(notifications)
    binding.qsPopupNotificationsRecyclerView.isVisible = true
  }

  override fun hideNotifications() {
    adapter.submitList(null)
    binding.qsPopupNotificationsRecyclerView.isGone = true
  }

  override fun showNotificationsEmptyError() {
    binding.noNotificationsTextView.isVisible = true
    binding.noNotificationsImageView.isVisible = true
  }

  override fun hideNotificationsEmptyError() {
    binding.noNotificationsTextView.isGone = true
    binding.noNotificationsImageView.isGone = true
  }

  private fun onToggleNotificationPinClicked(notification: PinnitNotification) {
    viewModel.dispatchEvent(TogglePinStatusClicked(notification))
  }

  private fun onNotificationClicked(notification: PinnitNotification) {
    viewModel.dispatchEvent(NotificationClicked(notification))
  }

  private fun onEditNotificationScheduleClicked(notification: PinnitNotification) {
    viewModel.dispatchEvent(EditNotificationScheduleClicked(notification))
  }

  private fun onRemoveNotificationScheduleClicked(notification: PinnitNotification) {
    viewModel.dispatchEvent(RemoveNotificationScheduleClicked(notification.uuid))
  }
}
