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
import androidx.recyclerview.widget.RecyclerView
import com.spotify.mobius.Mobius
import com.spotify.mobius.android.MobiusLoopViewModel
import com.spotify.mobius.functions.Function
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.notifications.adapter.NotificationsListAdapter
import dev.sasikanth.pinnit.utils.UtcClock
import kotlinx.android.synthetic.main.fragment_notifications.*
import java.util.UUID
import javax.inject.Inject

class NotificationsScreen : Fragment(R.layout.fragment_notifications), NotificationsScreenUi, NotificationsScreenUiActions {

  @Inject
  lateinit var utcClock: UtcClock

  @Inject
  lateinit var effectHandler: NotificationsScreenEffectHandler.Factory

  private val uiRender = NotificationsScreenUiRender(this)

  private val loop by lazy {
    Mobius.loop(
      NotificationsScreenUpdate(),
      effectHandler.create(this)
    )
  }

  private val adapter by lazy {
    NotificationsListAdapter.create(utcClock)
  }

  private val viewModel: MobiusLoopViewModel<NotificationsScreenModel, NotificationsScreenEvent, NotificationsScreenEffect, Nothing> by viewModels {
    object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MobiusLoopViewModel.create<NotificationsScreenModel, NotificationsScreenEvent, NotificationsScreenEffect, Nothing>(
          Function {
            loop
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

    notificationsRecyclerView.adapter = adapter
    notificationsRecyclerView.addItemDecoration(
      DividerItemDecoration(
        requireContext(),
        RecyclerView.VERTICAL
      )
    )

    viewModel.models.observe(viewLifecycleOwner, Observer { model ->
      uiRender.render(model)
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

  override fun openNotificationEditor(notificationUuid: UUID) {
    // TODO: Open notificaiton editor
  }
}
