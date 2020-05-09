package dev.sasikanth.pinnit.qspopup

import dev.sasikanth.pinnit.data.PinnitNotification

sealed class QsPopupViewEffect

data class OpenNotificationEditorViewEffect(val notification: PinnitNotification) : QsPopupViewEffect()
