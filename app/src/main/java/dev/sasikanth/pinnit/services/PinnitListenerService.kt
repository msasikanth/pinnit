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
import dev.sasikanth.pinnit.data.Result
import dev.sasikanth.pinnit.data.TemplateStyle
import dev.sasikanth.pinnit.data.source.PinnitRepository
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.shared.asBitmap
import dev.sasikanth.pinnit.utils.PinnitPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class PinnitListenerService : NotificationListenerService() {

  @Inject
  lateinit var pinnitPreferences: PinnitPreferences

  @Inject
  lateinit var pinnitRepository: PinnitRepository

  private val allowedApps: Set<String>
    get() = pinnitPreferences.allowedApps

  private val job = Job()
  private val coroutineContext = Dispatchers.Main
  private val coroutineScope = CoroutineScope(job + coroutineContext)

  override fun onCreate() {
    injector.inject(this)
    super.onCreate()
  }

  override fun onDestroy() {
    super.onDestroy()
    job.cancel()
  }

  override fun onNotificationPosted(sbn: StatusBarNotification?) {
    super.onNotificationPosted(sbn)
    if (sbn != null) {
      val notification = sbn.notification
      coroutineScope.launch {
        val notificationShouldBeFiltered = shouldFilterOutNotification(sbn)
        if (notificationShouldBeFiltered.not()) {
          withContext(Dispatchers.IO) {
            val appInfo = packageManager
                .getApplicationInfo(
                    sbn.packageName,
                    PackageManager.GET_META_DATA
                )

            val appLabel = packageManager
                .getApplicationLabel(appInfo)
                .toString()

            var title = notification
                .extras
                .getCharSequence(NotificationCompat.EXTRA_TITLE)
                ?.toString()
                .orEmpty()

            var text = notification
                .extras
                .getCharSequence(NotificationCompat.EXTRA_TEXT)
                ?.toString()
                .orEmpty()

            val messages = mutableListOf<Message>()
            val notificationIconUri = getNotificationIconUri(sbn, appInfo)

            // Identifying the template style for the notification
            val templateStyle = getNotificationTemplateStyle(notification)

            // Getting notification content like title, text, image etc.,
            when (templateStyle) {
              TemplateStyle.BigTextStyle -> {
                title = notification
                    .extras
                    .getCharSequence(Notification.EXTRA_TITLE_BIG)
                    ?.toString() ?: title

                text = notification
                    .extras
                    .getCharSequence(Notification.EXTRA_BIG_TEXT)
                    ?.toString() ?: text
              }

              TemplateStyle.InboxStyle -> {
                val extraLines = notification
                    .extras
                    .getCharSequenceArray(Notification.EXTRA_TEXT_LINES)

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
                val messagingStyle = NotificationCompat.MessagingStyle
                    .extractMessagingStyleFromNotification(notification)
                val extractedMessages = messagingStyle?.messages?.map {
                  Message(
                      it.person?.name?.toString().orEmpty(),
                      it.text.toString(),
                      it.timestamp
                  )
                }

                title = conversationTitle ?: title
                messages.clear()
                messages.addAll(extractedMessages.orEmpty())
              }

              TemplateStyle.BigPictureStyle -> {
                // TODO: Save image and add uri to db
              }

              else -> {
                // TODO: Add else branch for template checking
              }
            }

            val notificationKey = sbn.notification.hashCode().toLong()
            val notifItem = PinnitItem(
                notifKey = notificationKey,
                notifId = sbn.id,
                notifIcon = notificationIconUri,
                title = title,
                content = text,
                messages = messages,
                packageName = sbn.packageName,
                appLabel = appLabel,
                postedOn = sbn.postTime,
                template = templateStyle,
                isPinned = false,
                isCurrent = false
            )
            pinnitRepository.saveNotif(notifItem)
          }
        }
      }
    }
  }

  private fun getNotificationTemplateStyle(notification: Notification): TemplateStyle {
    val template = notification.extras.getString(Notification.EXTRA_TEMPLATE)

    return when {
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
  }

  private fun getNotificationIconUri(sbn: StatusBarNotification, appInfo: ApplicationInfo): Uri? {
    var uri: Uri? = null
    val largeIcon = sbn.notification.getLargeIcon()
    try {
      val picturesStorage = applicationContext
          .getExternalFilesDir(Environment.DIRECTORY_PICTURES)
      val notifIconFile =
          File(picturesStorage, appInfo.packageName + sbn.notification.getLargeIcon().hashCode())
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
  private fun shouldFilterOutNotification(sbn: StatusBarNotification): Boolean {
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
