package dev.sasikanth.pinnit.notifications

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialSharedAxis
import com.spotify.mobius.Mobius
import com.spotify.mobius.android.MobiusLoopViewModel
import com.spotify.mobius.functions.Consumer
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.about.AboutBottomSheet
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.di.DateTimeFormat
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.editor.EditorTransition
import dev.sasikanth.pinnit.editor.EditorTransition.SharedAxis
import dev.sasikanth.pinnit.notifications.adapter.NotificationPinItemAnimator
import dev.sasikanth.pinnit.notifications.adapter.NotificationsItemTouchHelper
import dev.sasikanth.pinnit.notifications.adapter.NotificationsListAdapter
import dev.sasikanth.pinnit.options.OptionsBottomSheet
import dev.sasikanth.pinnit.utils.UserClock
import dev.sasikanth.pinnit.utils.UtcClock
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_notifications.*
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class NotificationsScreen : Fragment(R.layout.fragment_notifications), NotificationsScreenUi {

  @Inject
  lateinit var utcClock: UtcClock

  @Inject
  lateinit var userClock: UserClock

  @Inject
  lateinit var effectHandler: NotificationsScreenEffectHandler.Factory

  @Inject
  @DateTimeFormat(DateTimeFormat.Type.ScheduleDateFormat)
  lateinit var scheduleDateFormatter: DateTimeFormatter

  @Inject
  @DateTimeFormat(DateTimeFormat.Type.ScheduleTimeFormat)
  lateinit var scheduleTimeFormatter: DateTimeFormatter

  private lateinit var adapter: NotificationsListAdapter

  private val uiRender = NotificationsScreenUiRender(this)

  private val viewModel: MobiusLoopViewModel<NotificationsScreenModel, NotificationsScreenEvent, NotificationsScreenEffect, NotificationScreenViewEffect> by viewModels {

    fun loop(viewEffectConsumer: Consumer<NotificationScreenViewEffect>) = Mobius.loop(
      NotificationsScreenUpdate(),
      effectHandler.create(viewEffectConsumer)
    )

    object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MobiusLoopViewModel.create(
          ::loop,
          NotificationsScreenModel.default(),
          NotificationsScreenInit()
        ) as T
      }
    }
  }
  private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
      notificationsRecyclerView.smoothScrollToPosition(0)
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    injector.inject(this)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    postponeEnterTransition()

    adapter = NotificationsListAdapter(
      utcClock = utcClock,
      userClock = userClock,
      scheduleDateFormatter = scheduleDateFormatter,
      scheduleTimeFormatter = scheduleTimeFormatter,
      onToggleNotificationPinClicked = ::onToggleNotificationPinClicked,
      onNotificationClicked = ::onNotificationClicked,
      onEditNotificationScheduleClicked = ::onEditNotificationScheduleClicked,
      onRemoveNotificationScheduleClicked = ::onRemoveNotificationScheduleClicked
    )
    adapter.registerAdapterDataObserver(adapterDataObserver)
    notificationsRecyclerView.adapter = adapter
    notificationsRecyclerView.itemAnimator = NotificationPinItemAnimator()
    notificationsRecyclerView.applySystemWindowInsetsToPadding(bottom = true, left = true, right = true)
    notificationsRecyclerView.doOnPreDraw { startPostponedEnterTransition() }

    val itemTouchHelperCallback = NotificationsItemTouchHelper(requireContext(), adapter) {
      viewModel.dispatchEvent(NotificationSwiped(it))
    }
    ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(notificationsRecyclerView)

    viewModel.models.observe(viewLifecycleOwner, uiRender::render)

    viewModel.viewEffects.setObserver(
      viewLifecycleOwner,
      ::viewEffectsHandler,
      { pausedViewEffects -> pausedViewEffects.forEach(::viewEffectsHandler) })

    requireActivity().bottomBar.setNavigationIcon(R.drawable.ic_pinnit_dark_mode)
    requireActivity().bottomBar.setContentActionEnabled(true)
    requireActivity().bottomBar.setContentActionText(R.string.create)
    requireActivity().bottomBar.setActionIcon(R.drawable.ic_pinnit_about)

    requireActivity().bottomBar.setNavigationOnClickListener {
      OptionsBottomSheet.show(requireActivity().supportFragmentManager)
    }
    requireActivity().bottomBar.setContentActionOnClickListener {
      openNotificationEditor(
        notification = null,
        navigatorExtras = null,
        editorTransition = SharedAxis
      )
    }
    requireActivity().bottomBar.setActionOnClickListener {
      showAbout()
    }
  }

  private fun viewEffectsHandler(viewEffect: NotificationScreenViewEffect?) {
    when (viewEffect) {
      is UndoNotificationDeleteViewEffect -> {
        Snackbar.make(notificationsRoot, R.string.notification_deleted, Snackbar.LENGTH_LONG)
          .setAnchorView(requireActivity().bottomBar)
          .setAction(R.string.undo) {
            viewModel.dispatchEvent(UndoNotificationDelete(viewEffect.notificationUuid))
          }
          .show()
      }
    }
  }

  override fun onDestroyView() {
    adapter.unregisterAdapterDataObserver(adapterDataObserver)
    notificationsRecyclerView.adapter = null
    notificationsRecyclerView.itemAnimator = null
    super.onDestroyView()
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

  private fun onNotificationClicked(view: View, notification: PinnitNotification) {
    openNotificationEditor(
      notification = notification,
      navigatorExtras = FragmentNavigatorExtras(view to view.transitionName),
      editorTransition = EditorTransition.ContainerTransform(view.transitionName)
    )
  }

  private fun onEditNotificationScheduleClicked(pinnitNotification: PinnitNotification) {
    openNotificationEditor(
      notification = pinnitNotification,
      navigatorExtras = null,
      editorTransition = SharedAxis
    )
  }

  private fun onRemoveNotificationScheduleClicked(pinnitNotification: PinnitNotification) {
    viewModel.dispatchEvent(RemoveNotificationScheduleClicked(pinnitNotification.uuid))
  }

  private fun openNotificationEditor(
    notification: PinnitNotification?,
    navigatorExtras: Navigator.Extras?,
    editorTransition: EditorTransition
  ) {
    val navDirections = NotificationsScreenDirections
      .actionNotificationsScreenToEditorScreen(
        notificationUuid = notification?.uuid?.toString(),
        editorTransition = editorTransition
      )

    if (navigatorExtras != null) {
      // In order to display the content behind when the
      // container transform is expanding
      exitTransition = Hold()

      findNavController().navigate(navDirections, navigatorExtras)
    } else {
      val backward = MaterialSharedAxis(MaterialSharedAxis.Y, false).apply {
        duration = 300
        addTarget(R.id.notificationsRoot)
        addTarget(R.id.editorRoot)
      }
      reenterTransition = backward

      val forward = MaterialSharedAxis(MaterialSharedAxis.Y, true).apply {
        duration = 300
        addTarget(R.id.notificationsRoot)
        addTarget(R.id.editorRoot)
      }
      exitTransition = forward
      findNavController().navigate(navDirections)
    }
  }

  private fun showAbout() {
    AboutBottomSheet.show(requireActivity().supportFragmentManager)
  }
}
