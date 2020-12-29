package dev.sasikanth.pinnit.editor

import java.time.LocalDate
import java.time.LocalTime

sealed class EditorScreenViewEffect

object CloseEditorView : EditorScreenViewEffect()

data class SetTitle(val title: String?) : EditorScreenViewEffect()

data class SetContent(val content: String?) : EditorScreenViewEffect()

object ShowConfirmExitEditorDialog : EditorScreenViewEffect()

object ShowConfirmDeleteDialog : EditorScreenViewEffect()

data class ShowDatePickerDialog(val date: LocalDate) : EditorScreenViewEffect()

data class ShowTimePickerDialog(val time: LocalTime) : EditorScreenViewEffect()
