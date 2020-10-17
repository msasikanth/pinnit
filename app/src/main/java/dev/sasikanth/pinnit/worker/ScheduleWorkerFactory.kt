package dev.sasikanth.pinnit.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.utils.DispatcherProvider
import dev.sasikanth.pinnit.utils.UserClock
import dev.sasikanth.pinnit.utils.notification.NotificationUtil

class ScheduleWorkerFactory(
  private val notificationRepository: NotificationRepository,
  private val notificationUtil: NotificationUtil,
  private val userClock: UserClock,
  private val dispatcherProvider: DispatcherProvider,
) : WorkerFactory() {

  override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {
    return when (workerClassName) {
      ScheduleWorker::class.java.name -> {
        ScheduleWorker(
          appContext,
          workerParameters,
          notificationRepository,
          notificationUtil,
          userClock,
          dispatcherProvider
        )
      }
      else -> null
    }
  }
}
