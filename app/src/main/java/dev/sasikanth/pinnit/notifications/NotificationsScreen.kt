package dev.sasikanth.pinnit.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.spotify.mobius.Mobius.loop
import com.spotify.mobius.android.MobiusLoopViewModel
import com.spotify.mobius.functions.Function
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import dev.sasikanth.pinnit.BuildConfig
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.notifications.adapter.NotificationPinItemAnimator
import dev.sasikanth.pinnit.notifications.adapter.NotificationsItemTouchHelper
import dev.sasikanth.pinnit.notifications.adapter.NotificationsListAdapter
import dev.sasikanth.pinnit.options.OptionsBottomSheet
import dev.sasikanth.pinnit.utils.UtcClock
import kotlinx.android.synthetic.main.activity_main.*
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
  private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
      notificationsRecyclerView.smoothScrollToPosition(0)
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    injector.inject(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val backward = MaterialSharedAxis.create(MaterialSharedAxis.Y, false)
    reenterTransition = backward

    val forward = MaterialSharedAxis.create(MaterialSharedAxis.Y, true)
    exitTransition = forward
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    adapter = NotificationsListAdapter(utcClock, ::onToggleNotificationPinClicked, ::onNotificationClicked)
    adapter.registerAdapterDataObserver(adapterDataObserver)
    notificationsRecyclerView.adapter = adapter
    notificationsRecyclerView.itemAnimator = NotificationPinItemAnimator()
    notificationsRecyclerView.applySystemWindowInsetsToPadding(bottom = true, left = true, right = true)

    val itemTouchHelperCallback = NotificationsItemTouchHelper(requireContext(), adapter) {
      viewModel.dispatchEvent(NotificationSwiped(it))
    }
    ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(notificationsRecyclerView)

    viewModel.models.observe(viewLifecycleOwner, Observer { model ->
      uiRender.render(model)
    })

    viewModel.viewEffects.setObserver(viewLifecycleOwner, Observer { viewEffect ->
      when (viewEffect) {
        is OpenNotificationEditorViewEffect -> {
          openNotificationEditor(notification = viewEffect.notification)
        }

        is UndoNotificationDeleteViewEffect -> {
          Snackbar.make(notificationsRoot, R.string.notification_deleted, Snackbar.LENGTH_LONG)
            .setAnchorView(requireActivity().bottomBar)
            .setAction(R.string.undo) {
              viewModel.dispatchEvent(UndoNotificationDelete(viewEffect.notificationUuid))
            }
            .show()
        }
      }
    })

    requireActivity().bottomBar.setNavigationIcon(R.drawable.ic_pinnit_dark_mode)
    requireActivity().bottomBar.setContentActionEnabled(true)
    requireActivity().bottomBar.setContentActionText(R.string.create)
    requireActivity().bottomBar.setActionIcon(R.drawable.ic_pinnit_about)

    requireActivity().bottomBar.setNavigationOnClickListener {
      OptionsBottomSheet.show(requireActivity().supportFragmentManager)
    }
    requireActivity().bottomBar.setContentActionOnClickListener {
      openNotificationEditor()
    }
    requireActivity().bottomBar.setActionOnClickListener {
      showAbout()
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

  private fun onNotificationClicked(notification: PinnitNotification) {
    viewModel.dispatchEvent(NotificationClicked(notification))
  }

  private fun openNotificationEditor(notification: PinnitNotification? = null) {
    val navDirections = NotificationsScreenDirections
      .actionNotificationsScreenToEditorScreen(
        notificationUuid = notification?.uuid?.toString()
      )

    findNavController().navigate(navDirections)
  }

  private fun showAbout() {
    val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
      setView(R.layout.pinnit_about_dialog)
    }.create()

    if (!dialog.isShowing) {
      dialog.show()
      dialog.findViewById<AppCompatTextView>(R.id.app_version)?.text =
        getString(R.string.app_version, BuildConfig.VERSION_NAME)
      dialog.findViewById<MaterialButton>(R.id.contact_support)
        ?.setOnClickListener {
          val intent = Intent(Intent.ACTION_SEND)
          intent.type = "text/plain"
          intent.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf("contact@sasikanth.dev")
          )
          intent.putExtra(
            Intent.EXTRA_SUBJECT,
            getString(
              R.string.support_subject,
              BuildConfig.VERSION_NAME
            )
          )
          startActivity(
            Intent.createChooser(
              intent,
              getString(R.string.send_email)
            )
          )
        }
    }
  }
}
