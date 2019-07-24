package dev.sasikanth.notif.shared

import android.text.format.DateUtils
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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

@BindingAdapter("notifIcon")
fun ImageView.setNotifIcon(notifItem: NotifItem?) {
    notifItem?.let {
        Glide.with(this)
            .load(it.iconBytes)
            .centerCrop()
            .circleCrop()
            .dontAnimate()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(this)
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
