package dev.sasikanth.notif.shared

import android.graphics.BitmapFactory
import android.text.format.DateUtils
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import dev.sasikanth.notif.R
import dev.sasikanth.notif.data.NotifItem
import dev.sasikanth.notif.data.TemplateStyle

@BindingAdapter("notifInfo")
fun AppCompatTextView.setNotifInfo(notifItem: NotifItem?) {
    notifItem?.let {
        val relativeTime = DateUtils.getRelativeTimeSpanString(
            notifItem.postedOn,
            System.currentTimeMillis(),
            DateUtils.SECOND_IN_MILLIS
        )
        text = "${notifItem.appLabel} • $relativeTime"
    }
}

@BindingAdapter("notifIcon")
fun ImageView.setNotifIcon(notifItem: NotifItem?) {
    notifItem?.let {
        val iconBytes = it.iconBytes
        if (iconBytes != null) {
            setImageBitmap(BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size))
        }
    }
}

@BindingAdapter("notifTitle")
fun AppCompatTextView.setNotifTitle(notifItem: NotifItem?) {
    notifItem?.let {
        text = notifItem.title
    }
}

@BindingAdapter("notifText")
fun AppCompatTextView.setNotifText(notifItem: NotifItem?) {
    notifItem?.let { notif ->
        if (notif.template == TemplateStyle.MessagingStyle) {
            val messagesBuilder = StringBuilder()
            val lastMessages = notif.messages.takeLast(5).sortedBy { it.timestamp }

            lastMessages.forEachIndexed { index, message ->
                val messageText = "${message.senderName}: ${message.message}"
                if (index == lastMessages.lastIndex) {
                    messagesBuilder.append(messageText)
                } else {
                    messagesBuilder.appendln(messageText)
                }
            }

            text = messagesBuilder.toString()
        } else {
            text = notif.text
        }
    }
}

@BindingAdapter("srcRes")
fun ImageView.setImageRes(drawableRes: Int) {
    setImageResource(drawableRes)
}

@BindingAdapter("isVisible")
fun View.setVisibility(isVisible: Boolean) {
    visibility = if (isVisible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("isOptionSelected")
fun View.setOptionSelected(isSelected: Boolean) {
    if (isSelected) {
        this.setBackgroundResource(R.drawable.option_item_selected)
    } else {
        this.background = null
    }
}
