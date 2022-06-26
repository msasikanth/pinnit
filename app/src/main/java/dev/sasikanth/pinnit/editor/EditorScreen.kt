package dev.sasikanth.pinnit.editor

import android.content.Context
import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.View.NO_ID
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.vectordrawable.graphics.drawable.SeekableAnimatedVectorDrawable
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialContainerTransform.FADE_MODE_OUT
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.Schedule
import dev.sasikanth.pinnit.data.ScheduleType
import dev.sasikanth.pinnit.databinding.FragmentNotificationEditorBinding
import dev.sasikanth.pinnit.di.DateTimeFormat
import dev.sasikanth.pinnit.di.DateTimeFormat.Type.ScheduleDateFormat
import dev.sasikanth.pinnit.di.DateTimeFormat.Type.ScheduleTimeFormat
import dev.sasikanth.pinnit.editor.EditorTransition.ContainerTransform
import dev.sasikanth.pinnit.editor.EditorTransition.SharedAxis
import dev.sasikanth.pinnit.utils.UserClock
import dev.sasikanth.pinnit.utils.UtcClock
import dev.sasikanth.pinnit.utils.resolveColor
import dev.sasikanth.pinnit.utils.reverse
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class EditorScreen : Fragment(), EditorScreenUi {

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

  private val viewModel: EditorScreenViewModel by viewModels()

  private val args by navArgs<EditorScreenArgs>()
  private val editorTransition by lazy { args.editorTransition }

  private val uiRender = EditorScreenUiRender(this)

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

  private var _binding: FragmentNotificationEditorBinding? = null
  private val binding get() = _binding!!
  private val scheduleViewBinding get() = binding.scheduleView

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

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    _binding = FragmentNotificationEditorBinding.inflate(layoutInflater, container, false)
    return _binding?.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    configBottomBar()
    configTitleEditText()
    configContentEditText()

    viewModelObservers()

    binding.toolbar.applySystemWindowInsetsToPadding(top = true, left = true, right = true)

    val toolbarTitle = if (args.notificationUuid == null) {
      getString(R.string.toolbar_title_create)
    } else {
      getString(R.string.toolbar_title_edit)
    }
    binding.toolbarTitleTextView.text = toolbarTitle

    if (editorTransition is ContainerTransform) {
      binding.editorRoot.transitionName = (editorTransition as ContainerTransform).transitionName
    }
    binding.editorScrollView.applySystemWindowInsetsToPadding(bottom = true, left = true, right = true)

    scheduleViewBinding.addRemoveScheduleButton.setImageDrawable(seekableAvd)
  }

  override fun onDestroyView() {
    _binding = null
    super.onDestroyView()
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
    binding.bottomBar.setNavigationIcon(R.drawable.ic_arrow_back)
    binding.bottomBar.setContentActionEnabled(false)
    binding.bottomBar.setContentActionText(contentActionText = null)
    binding.bottomBar.setActionIcon(null)

    binding.bottomBar.setNavigationOnClickListener {
      viewModel.dispatchEvent(BackClicked)
    }
    binding.bottomBar.setContentActionOnClickListener {
      viewModel.dispatchEvent(SaveClicked)
    }
    binding.bottomBar.setActionOnClickListener {
      viewModel.dispatchEvent(DeleteNotificationClicked)
    }
  }

  private fun configTitleEditText() {
    binding.titleEditText.doAfterTextChanged { viewModel.dispatchEvent(TitleChanged(it?.toString().orEmpty())) }
    binding.titleEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
    binding.titleEditText.inputType = EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
    binding.titleEditText.setHorizontallyScrolling(false)
    binding.titleEditText.maxLines = 5
  }

  private fun configContentEditText() {
    binding.contentEditText.movementMethod = BetterLinkMovementMethod.getInstance()
    binding.contentEditText.doAfterTextChanged { viewModel.dispatchEvent(ContentChanged(it?.toString())) }
  }

  private fun setTitleText(title: String?) {
    binding.titleEditText.setText(title)
    binding.titleEditText.requestFocus()
    binding.titleEditText.setSelection(title?.length ?: 0)

    binding.titleEditText.postDelayed({
      val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
      imm?.showSoftInput(binding.titleEditText, InputMethodManager.SHOW_IMPLICIT)
    }, 250)
  }

  private fun setContentText(content: String?) {
    binding.contentEditText.setText(content)
    Linkify.addLinks(binding.contentEditText, Linkify.WEB_URLS)
  }

  private fun closeEditor() {
    if (findNavController().currentDestination?.id == R.id.editorScreen) {
      val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
      imm?.hideSoftInputFromWindow(binding.titleEditText.windowToken, 0)

      findNavController().navigateUp()
    }
  }

  override fun enableSave() {
    binding.bottomBar.setContentActionEnabled(true)
  }

  override fun disableSave() {
    binding.bottomBar.setContentActionEnabled(false)
  }

  override fun renderSaveActionButtonText() {
    binding.bottomBar.setContentActionText(R.string.save)
  }

  override fun renderSaveAndPinActionButtonText() {
    binding.bottomBar.setContentActionText(R.string.save_and_pin)
  }

  override fun showDeleteButton() {
    binding.bottomBar.setActionIcon(R.drawable.ic_pinnit_delete)
  }

  override fun hideDeleteButton() {
    binding.bottomBar.setActionIcon(null)
  }

  override fun showScheduleView() {
    if (seekableAvd.isRunning.not()) {
      // Go to the end state of the AVD, in this case we will be showing delete icon at end
      seekableAvd.currentPlayTime = seekableAvd.totalDuration
    }

    scheduleViewBinding.addRemoveScheduleButton.setOnClickListener {
      seekableAvd.reverse()
      viewModel.dispatchEvent(RemoveScheduleClicked)
    }

    scheduleViewBinding.scheduleHeadingTextView.isGone = true
    scheduleViewBinding.scheduleDateButton.isVisible = true
    scheduleViewBinding.scheduleTimeButton.isVisible = true
    scheduleViewBinding.repeatEveryCheckBox.isVisible = true
    scheduleViewBinding.repeatEveryButtonGroup.isVisible = true
  }

  override fun renderScheduleDateTime(scheduleDate: LocalDate, scheduleTime: LocalTime) {
    scheduleViewBinding.scheduleDateButton.text = scheduleDateFormatter.format(scheduleDate)
    scheduleViewBinding.scheduleTimeButton.text = scheduleTimeFormatter.format(scheduleTime)

    scheduleViewBinding.scheduleDateButton.setOnClickListener {
      viewModel.dispatchEvent(ScheduleDateClicked)
    }

    scheduleViewBinding.scheduleTimeButton.setOnClickListener {
      viewModel.dispatchEvent(ScheduleTimeClicked)
    }
  }

  override fun renderScheduleRepeat(scheduleType: ScheduleType?, hasValidScheduleResult: Boolean) {
    scheduleViewBinding.repeatEveryButtonGroup.clearChecked()
    // To avoid any un-necessary even triggers of schedule type change
    // when schedule repeat is not set. so we are removing listeners on every model changes
    // and resetting it if schedule type is available.
    scheduleViewBinding.repeatEveryButtonGroup.clearOnButtonCheckedListeners()

    val hasScheduleType = scheduleType != null

    scheduleViewBinding.repeatEveryCheckBox.isChecked = hasScheduleType

    scheduleViewBinding.repeatEveryCheckBox.isEnabled = hasValidScheduleResult
    scheduleViewBinding.repeatDailyButton.isEnabled = hasScheduleType && hasValidScheduleResult
    scheduleViewBinding.repeatWeeklyButton.isEnabled = hasScheduleType && hasValidScheduleResult
    scheduleViewBinding.repeatMonthlyButton.isEnabled = hasScheduleType && hasValidScheduleResult

    scheduleViewBinding.repeatEveryCheckBox.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked.not())
        viewModel.dispatchEvent(ScheduleRepeatUnchecked)
      else
        viewModel.dispatchEvent(ScheduleRepeatChecked)
    }

    if (scheduleType != null) {
      scheduleViewBinding.repeatEveryButtonGroup.check(scheduleTypeToButtonId.getValue(scheduleType))
      scheduleViewBinding.repeatEveryButtonGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
        if (checkedId != NO_ID && isChecked) {
          viewModel.dispatchEvent(ScheduleTypeChanged(buttonIdToScheduleType.getValue(checkedId)))
        }
      }
    }
  }

  override fun showScheduleWarning() {
    scheduleViewBinding.scheduleWarningContainer.isVisible = true
    scheduleViewBinding.scheduleWarningTextView.text = requireContext().getString(R.string.editor_schedule_past_warning)
  }

  override fun hideScheduleWarning() {
    scheduleViewBinding.scheduleWarningContainer.isGone = true
  }

  override fun hideScheduleView() {
    scheduleViewBinding.addRemoveScheduleButton.setOnClickListener {
      // Start animating the add icon to delete icon
      seekableAvd.start()

      val schedule = Schedule.default(userClock)
      viewModel.dispatchEvent(AddScheduleClicked(schedule))
    }

    scheduleViewBinding.scheduleDateButton.isGone = true
    scheduleViewBinding.scheduleTimeButton.isGone = true

    scheduleViewBinding.scheduleWarningContainer.isGone = true

    scheduleViewBinding.repeatEveryCheckBox.isGone = true
    scheduleViewBinding.repeatEveryCheckBox.setOnCheckedChangeListener(null)

    scheduleViewBinding.repeatEveryButtonGroup.isGone = true
    scheduleViewBinding.repeatEveryButtonGroup.clearOnButtonCheckedListeners()

    scheduleViewBinding.scheduleHeadingTextView.isVisible = true
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
    val currentDate = LocalDate.now(userClock).atStartOfDay(utcClock.zone)
    val dateToOpen = date.atStartOfDay(utcClock.zone)

    val startAt = currentDate.toInstant().toEpochMilli()
    val initialDate = if (dateToOpen.isAfter(currentDate)) {
      dateToOpen.toInstant().toEpochMilli()
    } else {
      startAt
    }

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
