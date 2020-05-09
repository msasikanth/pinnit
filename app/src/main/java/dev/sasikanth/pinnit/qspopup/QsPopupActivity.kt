package dev.sasikanth.pinnit.qspopup

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDeepLinkBuilder
import com.spotify.mobius.Mobius.loop
import com.spotify.mobius.android.MobiusLoopViewModel
import com.spotify.mobius.functions.Function
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.activity.MainActivity
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.editor.EditorScreenArgs
import dev.sasikanth.pinnit.notifications.adapter.NotificationPinItemAnimator
import dev.sasikanth.pinnit.notifications.adapter.NotificationsListAdapter
import dev.sasikanth.pinnit.utils.UtcClock
import kotlinx.android.synthetic.main.activity_qs_popup.*
import javax.inject.Inject

/**
 * This activity will act as "dialog" to be opened when QS tile is
 * clicked. The reason why I didn't use an actual dialog is because it's not
 * properly following the app theme (day/night).
 */
class QsPopupActivity : AppCompatActivity(R.layout.activity_qs_popup), QsPopupUi {

  @Inject
  lateinit var effectHandler: QsPopupEffectHandler.Factory

  @Inject
  lateinit var utcClock: UtcClock

  private val uiRender = QsPopupUiRenderer(this)

  private val viewModel: MobiusLoopViewModel<QsPopupModel, QsPopupEvent, QsPopupEffect, QsPopupViewEffect> by viewModels {
    object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MobiusLoopViewModel.create<QsPopupModel, QsPopupEvent, QsPopupEffect, QsPopupViewEffect>(
          Function { viewEffectConsumer ->
            loop(
              QsPopupUpdate(),
              effectHandler.create(viewEffectConsumer)
            )
          },
          QsPopupModel.default(),
          QsPopupInit()
        ) as T
      }
    }
  }

  private lateinit var adapter: NotificationsListAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    injector.inject(this)

    adapter = NotificationsListAdapter(
      utcClock = utcClock,
      onToggleNotificationPinClicked = ::onToggleNotificationPinClicked,
      onNotificationClicked = ::onNotificationClicked
    )
    qsPopupNotificationsRecyclerView.adapter = adapter
    qsPopupNotificationsRecyclerView.itemAnimator = NotificationPinItemAnimator()

    viewModel.models.observe(this, Observer { model ->
      uiRender.render(model)
    })

    viewModel.viewEffects.setObserver(this, Observer { viewEffect ->
      when (viewEffect) {
        is OpenNotificationEditorViewEffect -> openNotificationEditorView(viewEffect.notification)
      }
    })

    backgroundRoot.setOnClickListener { finish() }

    openAppButton.setOnClickListener {
      Intent(this, MainActivity::class.java).also {
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(it)
      }
    }

    createNotificationButton.setOnClickListener {
      openNotificationEditorView()
    }
  }

  private fun openNotificationEditorView(notification: PinnitNotification? = null) {
    NavDeepLinkBuilder(this)
      .setComponentName(MainActivity::class.java)
      .setGraph(R.navigation.main_nav_graph)
      .setDestination(R.id.editorScreen)
      .setArguments(EditorScreenArgs(notificationUuid = notification?.uuid?.toString()).toBundle())
      .createTaskStackBuilder()
      .startActivities()
  }

  override fun showNotifications(notifications: List<PinnitNotification>) {
    adapter.submitList(notifications)
    qsPopupNotificationsRecyclerView.isVisible = true
  }

  override fun hideNotifications() {
    adapter.submitList(null)
    qsPopupNotificationsRecyclerView.isGone = true
  }

  override fun showNotificationsEmptyError() {
    noNotificationsTextView.isVisible = true
    noNotificationsImageView.isVisible = true
  }

  override fun hideNotificationsEmptyError() {
    noNotificationsTextView.isGone = true
    noNotificationsImageView.isGone = true
  }

  private fun onToggleNotificationPinClicked(notification: PinnitNotification) {
    // TODO: Toggle notification pin status
  }

  private fun onNotificationClicked(notification: PinnitNotification) {
    viewModel.dispatchEvent(NotificationClicked(notification))
  }
}
