package dev.sasikanth.notif.shared

import android.text.format.DateUtils
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import dev.sasikanth.notif.data.NotifItem

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

@BindingAdapter("notifTitle")
fun AppCompatTextView.setNotifTitle(notifItem: NotifItem?) {
    notifItem?.let {
        text = notifItem.title
    }
}

@BindingAdapter("notifText")
fun AppCompatTextView.setNotifText(notifItem: NotifItem?) {
    notifItem?.let {
        text = it.text
    }
}
