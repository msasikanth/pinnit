package dev.sasikanth.pinnit.editor

import dev.sasikanth.pinnit.data.PinnitNotification

sealed class EditorScreenViewEffect

data class CloseEditor(val notification: PinnitNotification) : EditorScreenViewEffect()

data class SetTitle(val title: String) : EditorScreenViewEffect()

data class SetContent(val content: String?) : EditorScreenViewEffect()
