package dev.sasikanth.pinnit.editor

import android.content.Context
import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import android.view.View.NO_ID
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.vectordrawable.graphics.drawable.SeekableAnimatedVectorDrawable
import com.google.android.material.composethemeadapter.MdcTheme
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
import com.spotify.mobius.functions.Consumer
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.Schedule
import dev.sasikanth.pinnit.data.ScheduleType
import dev.sasikanth.pinnit.di.DateTimeFormat
import dev.sasikanth.pinnit.di.DateTimeFormat.Type.ScheduleDateFormat
import dev.sasikanth.pinnit.di.DateTimeFormat.Type.ScheduleTimeFormat
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.editor.EditorTransition.ContainerTransform
import dev.sasikanth.pinnit.editor.EditorTransition.SharedAxis
import dev.sasikanth.pinnit.utils.UserClock
import dev.sasikanth.pinnit.utils.UtcClock
import dev.sasikanth.pinnit.utils.resolveColor
import dev.sasikanth.pinnit.utils.reverse
import dev.sasikanth.pinnit.widgets.PinnitBottomBar
import dev.sasikanth.pinnit.widgets.PinnitBottomBarIconButton
import dev.sasikanth.pinnit.widgets.PinnitButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_notification_editor.*
import kotlinx.android.synthetic.main.view_schedule.*
import kotlinx.android.synthetic.main.view_schedule.view.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
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

  @Inject
  @DateTimeFormat(ScheduleDateFormat)
  lateinit var scheduleDateFormatter: DateTimeFormatter

  @Inject
  @DateTimeFormat(ScheduleTimeFormat)
  lateinit var scheduleTimeFormatter: DateTimeFormatter

  private val args by navArgs<EditorScreenArgs>()
  private val editorTransition by lazy { args.editorTransition }

  private val uiRender = EditorScreenUiRender(this)

  private val viewModel: MobiusLoopViewModel<EditorScreenModel, EditorScreenEvent, EditorScreenEffect, EditorScreenViewEffect> by viewModels {

    val notificationUuid = if (args.notificationUuid != null) {
      UUID.fromString(args.notificationUuid)
    } else {
      null
    }

    fun loop(viewEffectConsumer: Consumer<EditorScreenViewEffect>) = Mobius.loop(
      EditorScreenUpdate(),
      effectHandler.create(viewEffectConsumer)
    )

    object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MobiusLoopViewModel.create(
          ::loop,
          EditorScreenModel.default(
            notificationUuid = notificationUuid,
            content = args.notificationContent,
            title = args.notificationTitle
          ),
          EditorScreenInit()
        ) as T
      }
    }
  }

  private val scheduleTypeToButtonId = mapOf(
    ScheduleType.Daily to R.id.repeatDailyButton,
    ScheduleType.Weekly to R.id.repeatWeeklyButton,
    ScheduleType.Monthly to R.id.repeatMonthlyButton
  )

  private val buttonIdToScheduleType = mapOf(
    R.id.repeatDailyButton to ScheduleType.Daily,
    R.id.repeatWeeklyButton to ScheduleType.Weekly,
    R.id.repeatMonthlyButton to ScheduleType.Monthly
  )

  private val seekableAvd by lazy {
    SeekableAnimatedVectorDrawable.create(requireContext(), R.drawable.avd_add_to_delete)!!
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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    configBottomBar()
    configTitleEditText()
    configContentEditText()

    viewModelObservers()

    if (editorTransition is ContainerTransform) {
      editorRoot.transitionName = (editorTransition as ContainerTransform).transitionName
    }
    editorScrollView.applySystemWindowInsetsToPadding(bottom = true, left = true, right = true)

    scheduleView.addRemoveScheduleButton.setImageDrawable(seekableAvd)
  }

  private fun viewModelObservers() {
    viewModel.models.observe(viewLifecycleOwner, { model ->
      uiRender.render(model)
    })

    viewModel.viewEffects.setObserver(viewLifecycleOwner, ::viewEffectsHandler, { pausedViewEffects ->
      pausedViewEffects.forEach(::viewEffectsHandler)
    })
  }

  private fun viewEffectsHandler(viewEffect: EditorScreenViewEffect?) {
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
  }

  private fun configBottomBar() {
    requireActivity().bottomBar.isVisible = true
    requireActivity().bottomBarOld.isGone = true

    requireActivity().bottomBarOld.setNavigationIcon(R.drawable.ic_arrow_back)
    requireActivity().bottomBarOld.setContentActionEnabled(false)
    requireActivity().bottomBarOld.setContentActionText(contentActionText = null)
    requireActivity().bottomBarOld.setActionIcon(null)

    requireActivity().bottomBarOld.setNavigationOnClickListener {
      viewModel.dispatchEvent(BackClicked)
    }
    requireActivity().bottomBarOld.setContentActionOnClickListener {
      viewModel.dispatchEvent(SaveClicked)
    }
    requireActivity().bottomBarOld.setActionOnClickListener {
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

  override fun renderPinnitBottomBar(model: EditorScreenModel) {
    requireActivity().bottomBar.setContent {
      MdcTheme {
        val contentButtonText = if (model.isNotificationLoaded || model.hasSchedule) {
          stringResource(id = R.string.save)
        } else {
          stringResource(id = R.string.save_and_pin)
        }

        PinnitBottomBar(
          navigationIcon = {
            PinnitBottomBarIconButton(
              onClick = { viewModel.dispatchEvent(BackClicked) }
            ) {
              Icon(painterResource(id = R.drawable.ic_arrow_back), contentDescription = null)
            }
          },
          content = {
            PinnitButton(
              modifier = Modifier.fillMaxWidth(),
              enabled = model.hasNotificationTitle && model.hasValidScheduleResult &&
                  (model.hasTitleAndContentChanged || model.hasScheduleChanged),
              onClick = { viewModel.dispatchEvent(SaveClicked) }
            ) {
              Text(text = contentButtonText.uppercase())
            }
          },
          actionIcon = if (model.isNotificationLoaded) {
            {
              PinnitBottomBarIconButton(
                onClick = { viewModel.dispatchEvent(DeleteNotificationClicked) }
              ) {
                Icon(painterResource(id = R.drawable.ic_pinnit_delete), contentDescription = null)
              }
            }
          } else null
        )
      }
    }
  }

  override fun showScheduleView() {
    if (seekableAvd.isRunning.not()) {
      // Go to the end state of the AVD, in this case we will be showing delete icon at end
      seekableAvd.currentPlayTime = seekableAvd.totalDuration
    }

    scheduleView.addRemoveScheduleButton.setOnClickListener {
      seekableAvd.reverse()
      viewModel.dispatchEvent(RemoveScheduleClicked)
    }

    scheduleView.scheduleHeadingTextView.isGone = true
    scheduleView.scheduleDateButton.isVisible = true
    scheduleView.scheduleTimeButton.isVisible = true
    scheduleView.repeatEveryCheckBox.isVisible = true
    scheduleView.repeatEveryButtonGroup.isVisible = true
  }

  override fun renderScheduleDateTime(scheduleDate: LocalDate, scheduleTime: LocalTime) {
    scheduleDateButton.text = scheduleDateFormatter.format(scheduleDate)
    scheduleTimeButton.text = scheduleTimeFormatter.format(scheduleTime)

    scheduleView.scheduleDateButton.setOnClickListener {
      viewModel.dispatchEvent(ScheduleDateClicked)
    }

    scheduleView.scheduleTimeButton.setOnClickListener {
      viewModel.dispatchEvent(ScheduleTimeClicked)
    }
  }

  override fun renderScheduleRepeat(scheduleType: ScheduleType?, hasValidScheduleResult: Boolean) {
    scheduleView.repeatEveryButtonGroup.clearChecked()
    // To avoid any un-necessary even triggers of schedule type change
    // when schedule repeat is not set. so we are removing listeners on every model changes
    // and resetting it if schedule type is available.
    scheduleView.repeatEveryButtonGroup.clearOnButtonCheckedListeners()

    val hasScheduleType = scheduleType != null

    scheduleView.repeatEveryCheckBox.isChecked = hasScheduleType

    scheduleView.repeatEveryCheckBox.isEnabled = hasValidScheduleResult
    scheduleView.repeatDailyButton.isEnabled = hasScheduleType && hasValidScheduleResult
    scheduleView.repeatWeeklyButton.isEnabled = hasScheduleType && hasValidScheduleResult
    scheduleView.repeatMonthlyButton.isEnabled = hasScheduleType && hasValidScheduleResult

    scheduleView.repeatEveryCheckBox.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked.not())
        viewModel.dispatchEvent(ScheduleRepeatUnchecked)
      else
        viewModel.dispatchEvent(ScheduleRepeatChecked)
    }

    if (scheduleType != null) {
      scheduleView.repeatEveryButtonGroup.check(scheduleTypeToButtonId.getValue(scheduleType))
      scheduleView.repeatEveryButtonGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
        if (checkedId != NO_ID && isChecked) {
          viewModel.dispatchEvent(ScheduleTypeChanged(buttonIdToScheduleType.getValue(checkedId)))
        }
      }
    }
  }

  override fun showScheduleWarning() {
    scheduleView.scheduleWarningContainer.isVisible = true
    scheduleView.scheduleWarningTextView.text = requireContext().getString(R.string.editor_schedule_past_warning)
  }

  override fun hideScheduleWarning() {
    scheduleView.scheduleWarningContainer.isGone = true
  }

  override fun hideScheduleView() {
    scheduleView.addRemoveScheduleButton.setOnClickListener {
      // Start animating the add icon to delete icon
      seekableAvd.start()

      val schedule = Schedule.default(userClock)
      viewModel.dispatchEvent(AddScheduleClicked(schedule))
    }

    scheduleView.scheduleDateButton.isGone = true
    scheduleView.scheduleTimeButton.isGone = true

    scheduleView.scheduleWarningContainer.isGone = true

    scheduleView.repeatEveryCheckBox.isGone = true
    scheduleView.repeatEveryCheckBox.setOnCheckedChangeListener(null)

    scheduleView.repeatEveryButtonGroup.isGone = true
    scheduleView.repeatEveryButtonGroup.clearOnButtonCheckedListeners()

    scheduleView.scheduleHeadingTextView.isVisible = true
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
      val localDate = instant.atZone(utcClock.zone).toLocalDate()

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
