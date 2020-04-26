package dev.sasikanth.pinnit.editor

sealed class EditorScreenViewEffect

object CloseEditor : EditorScreenViewEffect()

data class SetTitle(val title: String) : EditorScreenViewEffect()

data class SetContent(val content: String?) : EditorScreenViewEffect()
