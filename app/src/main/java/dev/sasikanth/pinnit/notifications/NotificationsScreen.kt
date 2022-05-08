package dev.sasikanth.pinnit.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection.EndToStart
import androidx.compose.material.DismissValue
import androidx.compose.material.DismissValue.DismissedToStart
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.about.AboutBottomSheet
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.databinding.FragmentNotificationsBinding
import dev.sasikanth.pinnit.di.DateTimeFormat
import dev.sasikanth.pinnit.editor.EditorTransition
import dev.sasikanth.pinnit.editor.EditorTransition.SharedAxis
import dev.sasikanth.pinnit.notifications.adapter.NotificationsItemTouchHelper
import dev.sasikanth.pinnit.notifications.adapter.NotificationsListAdapter
import dev.sasikanth.pinnit.options.OptionsBottomSheet
import dev.sasikanth.pinnit.theme.PinnitTheme
import dev.sasikanth.pinnit.utils.PinnitDateTimeFormatter
import dev.sasikanth.pinnit.utils.UserClock
import dev.sasikanth.pinnit.utils.UtcClock
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsScreen : Fragment(), NotificationsScreenUi {

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

  @Inject
  lateinit var dateTimeFormat: PinnitDateTimeFormatter

  private val viewModel: NotificationsScreenViewModel by viewModels()

  private val uiRender = NotificationsScreenUiRender(this)

  private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
      //      binding.notificationsRecyclerView.smoothScrollToPosition(0)
    }
  }

  private var _binding: FragmentNotificationsBinding? = null
  private val binding get() = _binding!!

  private lateinit var adapter: NotificationsListAdapter

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
    return _binding?.root
  }

  @OptIn(ExperimentalMaterialApi::class)
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    postponeEnterTransition()

    binding.toolbar.applySystemWindowInsetsToPadding(top = true, left = true, right = true)

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
    //    binding.notificationsRecyclerView.adapter = adapter
    //    binding.notificationsRecyclerView.itemAnimator = NotificationPinItemAnimator()
    //    binding.notificationsRecyclerView.applySystemWindowInsetsToPadding(bottom = true, left = true, right = true)
    //    binding.notificationsRecyclerView.doOnPreDraw { startPostponedEnterTransition() }

    val itemTouchHelperCallback = NotificationsItemTouchHelper(requireContext(), adapter) {
      viewModel.dispatchEvent(NotificationSwiped(it))
    }
    //    ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.notificationsRecyclerView)

    viewModel.models.observe(viewLifecycleOwner) { model ->
      binding.notificationsRecyclerView.setContent {
        PinnitTheme {
          LazyColumn {
            items(model.notifications.orEmpty()) { notification ->
              val dismissState = rememberDismissState(
                confirmStateChange = {
                  if (it == DismissedToStart) viewModel.dispatchEvent(NotificationSwiped(notification))
                  it != DismissedToStart
                }
              )

              SwipeToDismiss(
                state = dismissState,
                directions = setOf(EndToStart),
                background = {
                  Box(
                    modifier = Modifier
                      .fillParentMaxSize()
                      .background(PinnitTheme.colors.rowBackground),
                  ) {
                    Icon(
                      modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(24.dp),
                      painter = painterResource(id = R.drawable.ic_pinnit_delete),
                      contentDescription = null,
                      tint = PinnitTheme.colors.onPrimary
                    )
                  }
                }
              ) {
                NotificationCard(
                  notification = notification,
                  pinnitDateTimeFormatter = dateTimeFormat,
                  onClick = {
                    val navDirections = NotificationsScreenDirections.actionNotificationsScreenToEditorScreen(
                      notificationUuid = notification.uuid,
                      notificationContent = notification.content,
                      notificationTitle = notification.title,
                      editorTransition = SharedAxis
                    )
                    findNavController().navigate(navDirections)
                  },
                  onTogglePinClick = {
                    onToggleNotificationPinClicked(notification = notification)
                  },
                  onEditScheduleClick = {
                    onEditNotificationScheduleClicked(notification)
                  },
                  onRemoveScheduleClick = {
                    onRemoveNotificationScheduleClicked(notification)
                  }
                )
              }

              Divider(
                color = PinnitTheme.colors.divider
              )
            }
          }
        }
      }
    }

    viewModel.viewEffects.setObserver(
      viewLifecycleOwner,
      ::viewEffectsHandler,
      { pausedViewEffects -> pausedViewEffects.forEach(::viewEffectsHandler) })

    binding.bottomBar.setNavigationOnClickListener {
      OptionsBottomSheet.show(requireActivity().supportFragmentManager)
    }
    binding.bottomBar.setContentActionOnClickListener {
      openNotificationEditor(
        notification = null,
        navigatorExtras = null,
        editorTransition = SharedAxis
      )
    }
    binding.bottomBar.setActionOnClickListener {
      showAbout()
    }
  }

  private fun viewEffectsHandler(viewEffect: NotificationScreenViewEffect?) {
    when (viewEffect) {
      is UndoNotificationDeleteViewEffect -> {
        Snackbar.make(binding.notificationsRoot, R.string.notification_deleted, Snackbar.LENGTH_LONG)
          .setAnchorView(binding.bottomBar)
          .setAction(R.string.undo) {
            viewModel.dispatchEvent(UndoNotificationDelete(viewEffect.notificationUuid))
          }
          .show()
      }
    }
  }

  override fun onDestroyView() {
    adapter.unregisterAdapterDataObserver(adapterDataObserver)
    //    binding.notificationsRecyclerView.adapter = null
    //    binding.notificationsRecyclerView.itemAnimator = null
    super.onDestroyView()
  }

  override fun showNotifications(notifications: List<PinnitNotification>) {
    binding.notificationsRecyclerView.isVisible = true
    adapter.submitList(notifications)
  }

  override fun showNotificationsEmptyError() {
    binding.noNotificationsTextView.isVisible = true
    binding.noNotificationsImageView.isVisible = true
  }

  override fun hideNotificationsEmptyError() {
    binding.noNotificationsTextView.isGone = true
    binding.noNotificationsImageView.isGone = true
  }

  override fun hideNotifications() {
    binding.notificationsRecyclerView.isGone = true
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
        notificationUuid = notification?.uuid,
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
