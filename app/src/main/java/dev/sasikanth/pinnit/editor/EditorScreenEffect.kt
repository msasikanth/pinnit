package dev.sasikanth.pinnit.editor

import java.util.UUID

sealed class EditorScreenEffect

data class LoadNotification(val uuid: UUID) : EditorScreenEffect()

data class SaveNotificationAndCloseEditor(val title: String, val content: String?) : EditorScreenEffect()
