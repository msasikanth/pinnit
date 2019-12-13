package dev.sasikanth.pinnit.data.source.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import dev.sasikanth.pinnit.data.PinnitItem
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PinnitDatabaseTest {

  private lateinit var pinnitDatabase: PinnitDatabase
  private lateinit var pinnitDao: PinnitDao

  private val pinnitItem = PinnitItem(
      notifKey = "",
      notifId = 1402,
      packageName = "",
      appLabel = "",
      postedOn = 0L
  )

  @Before
  fun createDb() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    pinnitDatabase = Room.inMemoryDatabaseBuilder(context, PinnitDatabase::class.java)
        .allowMainThreadQueries()
        .build()

    pinnitDao = pinnitDatabase.pinnitDao
  }

  @Test
  fun getNotificationById() = runBlocking {
    val id = pinnitDao.insertNotifItem(pinnitItem)
    val notification = pinnitDao.getNotificationById(id)
    assertThat(notification).isNotNull()
  }

  @Test
  fun getNotificationByDefaultId() = runBlocking {
    pinnitDao.insertNotifItem(pinnitItem)
    val notification = pinnitDao.getNotificationById(0)
    assertThat(notification).isNull()
  }

  @Test
  fun getNotificationByKey() = runBlocking {
    pinnitDao.insertNotifItem(pinnitItem)
    val notification = pinnitDao.getNotificationByKey(pinnitItem.notifKey)
    assertThat(notification).isNotNull()
  }

  @Test
  fun deleteNotificationById() = runBlocking {
    val id = pinnitDao.insertNotifItem(pinnitItem)
    pinnitDao.deleteNotifById(id)
    assertThat(pinnitDao.getNotificationById(id)).isNull()
  }

  @Test
  fun updateNotificationPinStatus() = runBlocking {
    val id = pinnitDao.insertNotifItem(pinnitItem)
    val notification = pinnitDao.getNotificationById(id)
    if (notification != null) {
      assertThat(notification.isPinned).isFalse()
      pinnitDao.updateNotifPinStatus(id, true)
      val updateNotification = pinnitDao.getNotificationById(id)
      assertThat(updateNotification?.isPinned).isTrue()
    }
  }

  @After
  @Throws(IOException::class)
  fun closeDb() {
    pinnitDatabase.close()
  }
}
