package dev.sasikanth.pinnit.editor

import android.os.Parcel
import com.google.android.material.datepicker.CalendarConstraints
import dev.sasikanth.pinnit.utils.UserClock
import dev.sasikanth.pinnit.utils.UtcClock
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

/**
 * Only enables date selection in [com.google.android.material.datepicker.MaterialDatePicker]
 * when the date is greater or equal to the current date.
 */
class CurrentDateValidator @Inject constructor(
  userClock: UserClock,
  private val utcClock: UtcClock
) : CalendarConstraints.DateValidator {

  private val currentDate = LocalDate.now(userClock)

  override fun describeContents(): Int {
    return 0
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeInt(flags)
  }

  override fun isValid(date: Long): Boolean {
    val instant = Instant.ofEpochMilli(date)
    val localDate = instant.atZone(utcClock.zone).toLocalDate()

    return localDate >= currentDate
  }
}
