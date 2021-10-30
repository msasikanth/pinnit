package dev.sasikanth.pinnit.di

import javax.inject.Qualifier

@Qualifier
annotation class DateTimeFormat(val value: Type) {

  enum class Type {
    ScheduleDateFormat,
    ScheduleTimeFormat
  }
}
