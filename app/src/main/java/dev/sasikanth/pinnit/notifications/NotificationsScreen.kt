package dev.sasikanth.pinnit.notifications

import android.content.Context
import android.os.Bundle
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.spotify.mobius.Mobius.loop
import com.spotify.mobius.android.MobiusLoopViewModel
import com.spotify.mobius.functions.Function
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.notifications.adapter.NotificationsItemTouchHelper
import dev.sasikanth.pinnit.notifications.adapter.NotificationsListAdapter
import dev.sasikanth.pinnit.utils.UtcClock
import kotlinx.android.synthetic.main.fragment_notifications.*
import javax.inject.Inject

class NotificationsScreen : Fragment(R.layout.fragment_notifications), NotificationsScreenUi {

  @Inject
  lateinit var utcClock: UtcClock

  @Inject
  lateinit var effectHandler: NotificationsScreenEffectHandler.Factory

  private lateinit var adapter: NotificationsListAdapter

  private val uiRender = NotificationsScreenUiRender(this)

  private val viewModel: MobiusLoopViewModel<NotificationsScreenModel, NotificationsScreenEvent, NotificationsScreenEffect, NotificationScreenViewEffect> by viewModels {
    object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MobiusLoopViewModel.create<NotificationsScreenModel, NotificationsScreenEvent, NotificationsScreenEffect, NotificationScreenViewEffect>(
          Function { viewEffectConsumer ->
            loop(
              NotificationsScreenUpdate(),
              effectHandler.create(viewEffectConsumer)
            )
          },
          NotificationsScreenModel.default(),
          NotificationsScreenInit()
        ) as T
      }
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    injector.inject(this)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    adapter = NotificationsListAdapter(utcClock, ::onToggleNotificationPinClicked, ::onNotificationClicked)
    notificationsRecyclerView.adapter = adapter
    notificationsRecyclerView.addItemDecoration(
      DividerItemDecoration(
        requireContext(),
        RecyclerView.VERTICAL
      )
    )

    val itemTouchHelperCallback = NotificationsItemTouchHelper(requireContext(), adapter) {
      viewModel.dispatchEvent(NotificationSwiped(it))
    }
    ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(notificationsRecyclerView)

    viewModel.models.observe(viewLifecycleOwner, Observer { model ->
      uiRender.render(model)
    })

    viewModel.viewEffects.setObserver(viewLifecycleOwner, Observer {
      when (it) {
        is OpenNotificationEditorViewEffect -> {
          // TODO : Handle open notification editor
        }
      }
    })
  }

  override fun showNotifications(notifications: List<PinnitNotification>) {
    notificationsRecyclerView.isVisible = true
    adapter.submitList(notifications)
  }

  override fun showNotificationsEmptyError() {
    noNotificationsTextView.isVisible = true
    noNotificationsImageView.isVisible = true
  }

  override fun hideNotificationsEmptyError() {
    noNotificationsTextView.isGone = true
    noNotificationsImageView.isGone = true
  }

  override fun hideNotifications() {
    notificationsRecyclerView.isGone = true
    adapter.submitList(null)
  }

  private fun onToggleNotificationPinClicked(notification: PinnitNotification) {
    viewModel.dispatchEvent(TogglePinStatusClicked(notification))
  }

  private fun onNotificationClicked(notification: PinnitNotification) {
    viewModel.dispatchEvent(NotificationClicked(notification.uuid))
  }
}
