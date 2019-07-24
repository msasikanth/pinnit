package dev.sasikanth.notif.services

import android.app.Notification
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import dev.sasikanth.notif.data.NotifItem
import dev.sasikanth.notif.data.TemplateStyle
import dev.sasikanth.notif.data.source.NotifRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.io.ByteArrayOutputStream
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
    private val allowedApps = mutableSetOf(
        "com.readdle.spark"
    )

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
        Timber.i("Listener Connected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        isListenerConnected = false
        Timber.i("Listener Disconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let { statusBarNotification ->
            launch {
                val shouldBeFilteredOut = shouldBeFilteredOut(statusBarNotification)
                if (!shouldBeFilteredOut) {
                    // Notification shouldn't be filtered out
                    withContext(Dispatchers.IO) {
                        val packageManager = applicationContext.packageManager
                        val appInfo = packageManager.getApplicationInfo(
                            statusBarNotification.packageName, PackageManager.GET_META_DATA
                        )

                        val appLabel = packageManager.getApplicationLabel(appInfo).toString()

                        val title = statusBarNotification.notification.extras.getCharSequence(
                            NotificationCompat.EXTRA_TITLE
                        )?.toString().orEmpty()
                        val text = statusBarNotification.notification.extras.getCharSequence(
                            NotificationCompat.EXTRA_TEXT
                        )?.toString().orEmpty()

                        notifRepository.saveNotif(
                            NotifItem(
                                0,
                                statusBarNotification.key,
                                statusBarNotification.id,
                                bitmapToByteArray(statusBarNotification, appInfo),
                                title,
                                text,
                                emptyList(),
                                statusBarNotification.packageName,
                                appLabel,
                                statusBarNotification.postTime,
                                TemplateStyle.DefaultStyle,
                                false
                            )
                        )
                    }
                }
            }
        }
    }

    private fun bitmapToByteArray(
        sbn: StatusBarNotification,
        appInfo: ApplicationInfo
    ): ByteArray? {
        val largeIcon = sbn.notification.getLargeIcon()
        var iconBytes: ByteArray? = null
        try {
            val drawable = if (largeIcon != null) {
                largeIcon.loadDrawable(applicationContext)
            } else {
                packageManager.getApplicationIcon(appInfo)
            }
            val bitmap = createBitmap(drawable)
            ByteArrayOutputStream().use {
                bitmap.compress(CompressFormat.PNG, 0, it)
                iconBytes = it.toByteArray()
            }
        } catch (e: Exception) {
            iconBytes = null
        }
        return iconBytes
    }

    private fun createBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * Determines whether a notification should be filtered out or not based on
     * various conditions
     *
     * @return true if a notification should be filtered out
     */
    private suspend fun shouldBeFilteredOut(sbn: StatusBarNotification): Boolean {
        val notification = sbn.notification

        // TelegramX receives an acknowledgement notification if it's opened in another app,
        // ignoring those notifications.
        // TODO: Perfect it in future to deal with other apps as well
        if (sbn.packageName == "org.thunderdog.challegram") {
            if (notification.flags == 0x28) {
                return true
            }
        }

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

        val title = notification.extras.getCharSequence(Notification.EXTRA_TITLE)
        val text = notification.extras.getCharSequence(Notification.EXTRA_TEXT)

        return TextUtils.isEmpty(title) && TextUtils.isEmpty(text)
    }
}
