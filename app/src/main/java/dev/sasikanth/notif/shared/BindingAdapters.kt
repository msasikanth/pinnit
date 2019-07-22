package dev.sasikanth.notif.shared

import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import dev.sasikanth.notif.data.NotifItem

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
