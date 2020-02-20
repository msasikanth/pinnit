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
      key = 0,
      id = 1402,
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
  fun getNotificationByKey() = runBlocking {
    pinnitDao.insert(pinnitItem)
    val notification = pinnitDao.notification(pinnitItem.key)
    assertThat(notification).isNotNull()
  }

  @Test
  fun deleteNotificationByKey() = runBlocking {
    val key = pinnitDao.insert(pinnitItem)
    pinnitDao.delete(key)
    assertThat(pinnitDao.notification(key)).isNull()
  }

  @Test
  fun updateNotificationPinStatus() = runBlocking {
    val key = pinnitDao.insert(pinnitItem)
    val notification = pinnitDao.notification(key)
    if (notification != null) {
      assertThat(notification.isPinned).isFalse()
      pinnitDao.pinStatus(key, true)
      val updateNotification = pinnitDao.notification(key)
      assertThat(updateNotification?.isPinned).isTrue()
    }
  }

  @After
  @Throws(IOException::class)
  fun closeDb() {
    pinnitDatabase.close()
  }
}
