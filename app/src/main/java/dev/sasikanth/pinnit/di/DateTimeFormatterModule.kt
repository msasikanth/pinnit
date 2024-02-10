package dev.sasikanth.pinnit.di

import android.content.Context
import android.text.format.DateFormat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sasikanth.pinnit.di.DateTimeFormat.Type.ScheduleDateFormat
import dev.sasikanth.pinnit.di.DateTimeFormat.Type.ScheduleTimeFormat
import java.time.format.DateTimeFormatter

@InstallIn(SingletonComponent::class)
@Module
object DateTimeFormatterModule {

  @Provides
  @DateTimeFormat(ScheduleDateFormat)
  fun providesScheduleDateFormat(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern("MMM d")
  }

  @Provides
  @DateTimeFormat(ScheduleTimeFormat)
  fun providesScheduleTimeFormat(
    isClock24HourFormat: Boolean
  ): DateTimeFormatter {
    return if (isClock24HourFormat) {
      DateTimeFormatter.ofPattern("HH:mm")
    } else {
      DateTimeFormatter.ofPattern("h:mm a")
    }
  }

  @Provides
  fun providesIsClock24HourFormat(
    @ApplicationContext context: Context
  ): Boolean {
    return DateFormat.is24HourFormat(context)
  }
}
