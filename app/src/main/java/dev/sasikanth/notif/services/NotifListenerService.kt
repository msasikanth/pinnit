package dev.sasikanth.notif.services

import android.app.Notification
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import dev.sasikanth.notif.data.source.NotifRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class NotifListenerService : NotificationListenerService(), CoroutineScope {

    companion object {

        @Volatile
        private var instance: NotifListenerService? = null

        private var isListenerConnected = false
        private var isListenerCreated = false

        fun getInstanceIfConnected(): NotifListenerService? {
            synchronized(this) {
                return if (isListenerConnected) {
                    instance
                } else {
                    null
                }
            }
        }
    }

    private val notifRepository: NotifRepository by inject()

    private val job = Job()
    private val allowedApps = mutableSetOf<String>()

    init {
        instance = this
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate() {
        super.onCreate()
        isListenerCreated = true
    }

    override fun onDestroy() {
        super.onDestroy()
        isListenerCreated = false
        job.cancel()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        isListenerConnected = true
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        isListenerConnected = false
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let { statusBarNotification ->
            launch {
                val shouldBeFilteredOut = shouldBeFilteredOut(statusBarNotification)
                if (!shouldBeFilteredOut) {
                    // Notification shouldn't be filtered out
                    withContext(Dispatchers.IO) {
                    }
                }
            }
        }
    }

    /**
     * Determines whether a notification should be filtered out or not based on
     * various conditions
     *
     * @return true if a notification should be filtered out
     */
    private suspend fun shouldBeFilteredOut(sbn: StatusBarNotification): Boolean {
        val notification = sbn.notification

        // Checking if the notification package name is present in allowed apps list
        if (!allowedApps.contains(sbn.packageName)) {
            return true
        }

        // Ignoring notification if the package name matches with this app
        if (sbn.packageName == applicationContext.packageName) {
            return true
        }

        // Checking if a notification is clearable or not, if not return true to filter it
        if (!sbn.isClearable) {
            return true
        }

        val template = notification.extras.getString(NotificationCompat.EXTRA_TEMPLATE)
        if (template == Notification.MediaStyle::class.java.name) {
            return true
        } else if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                template == Notification.DecoratedMediaCustomViewStyle::class.java.name
            } else {
                false
            }
        ) {
            return true
        }

        // TelegramX receives an acknowledgement notification if it's opened in another app,
        // ignoring those notifications.
        // TODO: Perfect it in future to deal with other apps as well
        if (sbn.packageName == "org.thunderdog.challegram") {
            if (notification.flags == 0x28) {
                return true
            }
        }

        val title = notification.extras.getCharSequence(Notification.EXTRA_TITLE)
        val text = notification.extras.getCharSequence(Notification.EXTRA_TEXT)

        return TextUtils.isEmpty(title) && TextUtils.isEmpty(text)
    }
}
