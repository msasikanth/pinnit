package dev.sasikanth.pinnit.di

import dagger.Module
import dagger.Provides
import dev.sasikanth.pinnit.di.DateTimeFormat.Type.ScheduleDateFormat
import dev.sasikanth.pinnit.di.DateTimeFormat.Type.ScheduleTimeFormat
import java.time.format.DateTimeFormatter
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class DateTimeFormat(val value: Type) {

  enum class Type {
    ScheduleDateFormat,
    ScheduleTimeFormat
  }
}

@Module
object DateTimeFormatterModule {

  @Provides
  @DateTimeFormat(ScheduleDateFormat)
  fun providesScheduleDateFormat(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern("MMM d")
  }

  @Provides
  @DateTimeFormat(ScheduleTimeFormat)
  fun providesScheduleTimeFormat(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern("h:mm a")
  }
}
