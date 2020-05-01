package dev.sasikanth.pinnit.editor

import dev.sasikanth.pinnit.data.PinnitNotification

sealed class EditorScreenEvent

data class NotificationLoaded(val notification: PinnitNotification) : EditorScreenEvent()

data class TitleChanged(val title: String) : EditorScreenEvent()

data class ContentChanged(val content: String?) : EditorScreenEvent()

object SaveClicked : EditorScreenEvent()

object BackClicked : EditorScreenEvent()
