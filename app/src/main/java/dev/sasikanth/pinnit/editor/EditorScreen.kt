package dev.sasikanth.pinnit.editor

import android.content.Context
import android.os.Bundle
import android.text.util.Linkify
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialContainerTransform.FADE_MODE_OUT
import com.google.android.material.transition.MaterialSharedAxis
import com.spotify.mobius.Mobius
import com.spotify.mobius.android.MobiusLoopViewModel
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.ScheduleType
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.editor.EditorTransition.ContainerTransform
import dev.sasikanth.pinnit.editor.EditorTransition.SharedAxis
import dev.sasikanth.pinnit.utils.UserClock
import dev.sasikanth.pinnit.utils.UtcClock
import dev.sasikanth.pinnit.utils.resolveColor
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_notification_editor.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

class EditorScreen : Fragment(R.layout.fragment_notification_editor), EditorScreenUi {

  @Inject
  lateinit var effectHandler: EditorScreenEffectHandler.Factory

  @Inject
  lateinit var userClock: UserClock

  @Inject
  lateinit var utcClock: UtcClock

  @Inject
  lateinit var currentDateValidator: CurrentDateValidator

  private val args by navArgs<EditorScreenArgs>()
  private val editorTransition by lazy { args.editorTransition }

  private val uiRender = EditorScreenUiRender(this)

  private val viewModel: MobiusLoopViewModel<EditorScreenModel, EditorScreenEvent, EditorScreenEffect, EditorScreenViewEffect> by viewModels {
    object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val notificationUuid = if (args.notificationUuid != null) {
          UUID.fromString(args.notificationUuid)
        } else {
          null
        }
        return MobiusLoopViewModel.create<EditorScreenModel, EditorScreenEvent, EditorScreenEffect, EditorScreenViewEffect>(
          { viewEffectConsumer ->
            Mobius.loop(
              EditorScreenUpdate(),
              effectHandler.create(viewEffectConsumer)
            )
          },
          EditorScreenModel.default(notificationUuid = notificationUuid, content = args.notificationContent, title = args.notificationTitle),
          EditorScreenInit()
        ) as T
      }
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    injector.inject(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    when (editorTransition) {
      is SharedAxis -> sharedAxisTransition()
      is ContainerTransform -> containerTransformTransition()
    }

    requireActivity().onBackPressedDispatcher.addCallback(this) {
      viewModel.dispatchEvent(BackClicked)
    }
  }

  private fun containerTransformTransition() {
    sharedElementEnterTransition = MaterialContainerTransform().apply {
      drawingViewId = R.id.nav_host_fragment_container
      scrimColor = requireContext().resolveColor(colorRes = R.color.containerTransformScrim)
      fadeMode = FADE_MODE_OUT
    }
  }

  private fun sharedAxisTransition() {
    val forward = MaterialSharedAxis(MaterialSharedAxis.Y, true).apply {
      duration = 300
    }
    enterTransition = forward

    val backward = MaterialSharedAxis(MaterialSharedAxis.Y, false).apply {
      duration = 300
    }
    returnTransition = backward
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    configBottomBar()
    configTitleEditText()
    configContentEditText()

    viewModelObservers()

    if (editorTransition is ContainerTransform) {
      editorRoot.transitionName = (editorTransition as ContainerTransform).transitionName
    }
    editorScrollView.applySystemWindowInsetsToPadding(bottom = true, left = true, right = true)
  }

  private fun viewModelObservers() {
    viewModel.models.observe(viewLifecycleOwner, { model ->
      uiRender.render(model)
    })

    viewModel.viewEffects.setObserver(viewLifecycleOwner, { viewEffect ->
      when (viewEffect) {
        is SetTitle -> {
          setTitleText(viewEffect.title)
        }

        is SetContent -> {
          setContentText(viewEffect.content)
        }

        CloseEditorView -> {
          closeEditor()
        }

        ShowConfirmExitEditorDialog -> {
          showConfirmExitDialog()
        }

        ShowConfirmDeleteDialog -> {
          showConfirmDeleteDialog()
        }

        is ShowDatePickerDialog -> {
          showDatePickerDialog(viewEffect.date)
        }

        is ShowTimePickerDialog -> {
          showTimePickerDialog(viewEffect.time)
        }
      }
    })
  }

  private fun configBottomBar() {
    requireActivity().bottomBar.setNavigationIcon(R.drawable.ic_arrow_back)
    requireActivity().bottomBar.setContentActionEnabled(false)
    requireActivity().bottomBar.setContentActionText(contentActionText = null)
    requireActivity().bottomBar.setActionIcon(null)

    requireActivity().bottomBar.setNavigationOnClickListener {
      viewModel.dispatchEvent(BackClicked)
    }
    requireActivity().bottomBar.setContentActionOnClickListener {
      viewModel.dispatchEvent(SaveClicked)
    }
    requireActivity().bottomBar.setActionOnClickListener {
      viewModel.dispatchEvent(DeleteNotificationClicked)
    }
  }

  private fun configTitleEditText() {
    titleEditText.doAfterTextChanged { viewModel.dispatchEvent(TitleChanged(it?.toString().orEmpty())) }
    titleEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
    titleEditText.inputType = EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
    titleEditText.setHorizontallyScrolling(false)
    titleEditText.maxLines = 5
  }

  private fun configContentEditText() {
    contentEditText.movementMethod = BetterLinkMovementMethod.getInstance()
    contentEditText.doAfterTextChanged { viewModel.dispatchEvent(ContentChanged(it?.toString())) }
  }

  private fun setTitleText(title: String?) {
    titleEditText.setText(title)
    titleEditText.requestFocus()
    titleEditText.setSelection(title?.length ?: 0)

    titleEditText.postDelayed({
      val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
      imm?.showSoftInput(titleEditText, InputMethodManager.SHOW_IMPLICIT)
    }, 250)
  }

  private fun setContentText(content: String?) {
    contentEditText.setText(content)
    Linkify.addLinks(contentEditText, Linkify.WEB_URLS)
  }

  private fun closeEditor() {
    if (findNavController().currentDestination?.id == R.id.editorScreen) {
      val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
      imm?.hideSoftInputFromWindow(titleEditText.windowToken, 0)

      findNavController().navigateUp()
    }
  }

  override fun enableSave() {
    requireActivity().bottomBar.setContentActionEnabled(true)
  }

  override fun disableSave() {
    requireActivity().bottomBar.setContentActionEnabled(false)
  }

  override fun renderSaveActionButtonText() {
    requireActivity().bottomBar.setContentActionText(R.string.save)
  }

  override fun renderSaveAndPinActionButtonText() {
    requireActivity().bottomBar.setContentActionText(R.string.save_and_pin)
  }

  override fun showDeleteButton() {
    requireActivity().bottomBar.setActionIcon(R.drawable.ic_pinnit_delete)
  }

  override fun hideDeleteButton() {
    requireActivity().bottomBar.setActionIcon(null)
  }

  override fun showScheduleView(scheduleDate: LocalDate, scheduleTime: LocalTime, scheduleType: ScheduleType?) {
    // TODO: Show schedule view
  }

  override fun hideScheduleView() {
    // TODO: Hide schedule view
  }

  private fun showConfirmExitDialog() {
    MaterialAlertDialogBuilder(requireContext())
      .setTitle(getString(R.string.confirm_editor_exit_title))
      .setPositiveButton(R.string.confirm_editor_exit_positive_action) { _, _ ->
        viewModel.dispatchEvent(ConfirmedExit)
      }
      .setNegativeButton(R.string.confirm_editor_exit_negative_action) { _, _ ->
        // NO-OP
      }
      .show()
  }

  private fun showConfirmDeleteDialog() {
    MaterialAlertDialogBuilder(requireContext())
      .setTitle(R.string.confirm_delete_notification_title)
      .setMessage(R.string.confirm_delete_notification_desc)
      .setPositiveButton(R.string.confirm_delete_notification_positive_action) { _, _ ->
        viewModel.dispatchEvent(ConfirmDeleteNotification)
      }
      .setNegativeButton(R.string.confirm_delete_notification_negative_action) { _, _ ->
        // NO-OP
      }
      .show()
  }

  private fun showDatePickerDialog(date: LocalDate) {
    val currentDate = LocalDate.now(userClock)

    val initialDate = date.atStartOfDay(utcClock.zone).toInstant().toEpochMilli()
    val startAt = currentDate.atStartOfDay(utcClock.zone).toInstant().toEpochMilli()

    val calendarConstraints = CalendarConstraints.Builder()
      .setStart(startAt)
      .setOpenAt(initialDate)
      .setValidator(currentDateValidator)
      .build()

    val datePicker = MaterialDatePicker.Builder.datePicker()
      .setSelection(initialDate)
      .setCalendarConstraints(calendarConstraints)
      .build()

    datePicker.addOnPositiveButtonClickListener { selectedDate ->
      val instant = Instant.ofEpochMilli(selectedDate)
      val localDate = instant.atZone(userClock.zone).toLocalDate()

      viewModel.dispatchEvent(ScheduleDateChanged(localDate))
    }

    datePicker.show(parentFragmentManager, "ScheduleDatePickerDialog")
  }

  private fun showTimePickerDialog(time: LocalTime) {
    val timePicker = MaterialTimePicker.Builder()
      .setTimeFormat(TimeFormat.CLOCK_12H)
      .setHour(time.hour)
      .setMinute(time.minute)
      .build()

    timePicker.addOnPositiveButtonClickListener {
      val selectedTime = LocalTime.of(timePicker.hour, timePicker.minute)

      viewModel.dispatchEvent(ScheduleTimeChanged(selectedTime))
    }

    timePicker.show(parentFragmentManager, "ScheduleTimePickerDialog")
  }
}
