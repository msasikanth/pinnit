package dev.sasikanth.pinnit.services

import android.app.Notification
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import dev.sasikanth.pinnit.data.Message
import dev.sasikanth.pinnit.data.PinnitItem
import dev.sasikanth.pinnit.data.TemplateStyle
import dev.sasikanth.pinnit.data.source.PinnitRepository
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.shared.asBitmap
import dev.sasikanth.pinnit.utils.PinnitPreferences
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@Suppress("unused")
class PinnitListenerService : NotificationListenerService(), CoroutineScope {

    companion object {

        @Volatile
        private var instance: PinnitListenerService? = null

        private var isListenerConnected = false
        private var isListenerCreated = false

        fun getInstanceIfConnected(): PinnitListenerService? {
            synchronized(this) {
                return if (isListenerConnected) {
                    instance
                } else {
                    null
                }
            }
        }
    }

    @Inject
    lateinit var pinnitPreferences: PinnitPreferences

    @Inject
    lateinit var pinnitRepository: PinnitRepository

    private val job = Job()
    private val allowedApps: MutableSet<String>
        get() = pinnitPreferences.allowedApps

    init {
        instance = this
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate() {
        super.onCreate()
        injector.inject(this)

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
            val notification = statusBarNotification.notification
            launch {
                val shouldBeFilteredOut = shouldBeFilteredOut(statusBarNotification)
                if (!shouldBeFilteredOut) {
                    // Notification shouldn't be filtered out
                    withContext(Dispatchers.IO) {
                        val appInfo = packageManager.getApplicationInfo(
                            statusBarNotification.packageName, PackageManager.GET_META_DATA
                        )

                        val appLabel = packageManager.getApplicationLabel(appInfo).toString()
                        var title = notification.extras.getCharSequence(
                            NotificationCompat.EXTRA_TITLE
                        )?.toString().orEmpty()
                        var text = notification.extras.getCharSequence(
                            NotificationCompat.EXTRA_TEXT
                        )?.toString().orEmpty()

                        val messages = mutableListOf<Message>()
                        val notificationIconUri =
                            getNotificationIconUri(statusBarNotification, appInfo)
                        val template = notification.extras.getString(Notification.EXTRA_TEMPLATE)

                        val templateStyle =
                            when {
                                template == Notification.BigTextStyle::class.java.name -> TemplateStyle.BigTextStyle
                                template == Notification.BigPictureStyle::class.java.name -> TemplateStyle.BigPictureStyle
                                template == Notification.InboxStyle::class.java.name -> TemplateStyle.InboxStyle
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    template == Notification.DecoratedCustomViewStyle::class.java.name
                                } else {
                                    false
                                } -> TemplateStyle.DecoratedViewStyle
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    template == Notification.MessagingStyle::class.java.name
                                } else {
                                    false
                                } -> TemplateStyle.MessagingStyle
                                else -> TemplateStyle.DefaultStyle
                            }

                        when (templateStyle) {
                            TemplateStyle.BigTextStyle -> {
                                title = notification.extras
                                    .getCharSequence(Notification.EXTRA_TITLE_BIG)
                                    ?.toString() ?: title

                                text = notification.extras
                                    .getCharSequence(Notification.EXTRA_BIG_TEXT)
                                    ?.toString() ?: text
                            }
                            TemplateStyle.BigPictureStyle -> {
                                // TODO: Save image and add uri to db
                            }
                            TemplateStyle.InboxStyle -> {
                                val extraLines =
                                    notification.extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
                                text = extraLines?.joinToString(separator = "\n") ?: text
                            }
                            TemplateStyle.MessagingStyle -> {
                                val conversationTitle =
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        notification.extras
                                            .getCharSequence(Notification.EXTRA_CONVERSATION_TITLE)
                                            ?.toString()
                                    } else {
                                        null
                                    }
                                title = conversationTitle ?: title

                                val messagingStyle = NotificationCompat.MessagingStyle
                                    .extractMessagingStyleFromNotification(notification)
                                val extractedMessages = messagingStyle?.messages?.map {
                                    Message(
                                        it.person?.name?.toString().orEmpty(),
                                        it.text.toString(),
                                        it.timestamp
                                    )
                                }
                                messages.clear()
                                messages.addAll(extractedMessages.orEmpty())
                            }
                            else -> {
                                // TODO: Add else branch for template checking
                            }
                        }

                        val notifItem = PinnitItem(
                            _id = 0,
                            notifKey = statusBarNotification.key,
                            notifId = statusBarNotification.id,
                            notifIcon = notificationIconUri,
                            title = title,
                            text = text,
                            messages = messages,
                            packageName = statusBarNotification.packageName,
                            appLabel = appLabel,
                            postedOn = statusBarNotification.postTime,
                            template = templateStyle,
                            isPinned = false,
                            isCurrent = false // TODO: Set as current when notification is posted
                        )
                        pinnitRepository.saveNotif(notifItem)
                    }
                }
            }
        }
    }

    // TODO: Handle get current notifications
    fun getCurrentNotifications(): List<PinnitItem> {
        return emptyList()
    }

    // TODO: Save notification icon / app icon to storage, don't save if already exists.
    private fun getNotificationIconUri(
        sbn: StatusBarNotification,
        appInfo: ApplicationInfo
    ): Uri? {
        var uri: Uri? = null
        val largeIcon = sbn.notification.getLargeIcon()
        try {
            val picturesStorage =
                applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val notifIconFile = File(picturesStorage, appInfo.packageName + sbn.key)
            val iconDrawable = if (largeIcon != null) {
                largeIcon.loadDrawable(applicationContext)
            } else {
                packageManager.getApplicationIcon(appInfo)
            }

            // If icon does not exists create one, or else return the uri
            if (notifIconFile.exists().not()) {
                val bitmap = iconDrawable.asBitmap()
                FileOutputStream(notifIconFile).use {
                    bitmap.compress(CompressFormat.WEBP, 100, it)
                }
            }
            uri = notifIconFile.toUri()
        } catch (e: Exception) {
            // Do nothing
        }
        return uri
    }

    /**
     * Determines whether a notification should be filtered out or not based on
     * various conditions
     *
     * @return true if a notification should be filtered out
     */
    private fun shouldBeFilteredOut(sbn: StatusBarNotification): Boolean {
        // Filter if package name is not present in allowed apps list
        if (!allowedApps.contains(sbn.packageName)) {
            return true
        }

        // Filter if the notification package name matched pinnit app package name
        if (sbn.packageName == applicationContext.packageName) {
            return true
        }

        // Filter if a notification is not clearable
        if (!sbn.isClearable) {
            return true
        }

        val notification = sbn.notification

        val title = notification.extras.getCharSequence(Notification.EXTRA_TITLE)
        val text = notification.extras.getCharSequence(Notification.EXTRA_TEXT)

        // Filter if title and text both are empty or null
        if (title.isNullOrEmpty() && text.isNullOrEmpty()) {
            return true
        }

        // Filter if it's a group summary
        if ((notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0) {
            return true
        }

        // Filter media style notifications
        val template = notification.extras.getString(NotificationCompat.EXTRA_TEMPLATE)
        return if (template == Notification.MediaStyle::class.java.name) {
            true
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                template == Notification.DecoratedMediaCustomViewStyle::class.java.name
            } else {
                false
            }
        }
    }
}
