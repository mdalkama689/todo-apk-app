package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
  fun isSameDay(time1: Long, time2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
  }

  fun isToday(millis: Long): Boolean {
    return isSameDay(millis, System.currentTimeMillis())
  }

  fun isTomorrow(millis: Long): Boolean {
    val tomorrow = Calendar.getInstance().apply {
      add(Calendar.DAY_OF_YEAR, 1)
    }
    return isSameDay(millis, tomorrow.timeInMillis)
  }

  fun isYesterday(millis: Long): Boolean {
    val yesterday = Calendar.getInstance().apply {
      add(Calendar.DAY_OF_YEAR, -1)
    }
    return isSameDay(millis, yesterday.timeInMillis)
  }

  fun isOverdue(dueDateMillis: Long): Boolean {
    val endOfDueDay = Calendar.getInstance().apply {
      timeInMillis = dueDateMillis
      set(Calendar.HOUR_OF_DAY, 23)
      set(Calendar.MINUTE, 59)
      set(Calendar.SECOND, 59)
      set(Calendar.MILLISECOND, 999)
    }.timeInMillis
    return System.currentTimeMillis() > endOfDueDay
  }

  fun formatDueDate(millis: Long): String {
    return when {
      isToday(millis) -> "Today"
      isTomorrow(millis) -> "Tomorrow"
      isYesterday(millis) -> "Yesterday"
      else -> {
        val format = SimpleDateFormat("MMM d", Locale.getDefault())
        format.format(Date(millis))
      }
    }
  }

  fun formatFullDate(millis: Long): String {
    val format = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
    return format.format(Date(millis))
  }

  fun getTodayStart(): Long {
    return Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }.timeInMillis
  }

  fun getTodayEnd(): Long {
    return Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, 23)
      set(Calendar.MINUTE, 59)
      set(Calendar.SECOND, 59)
      set(Calendar.MILLISECOND, 999)
    }.timeInMillis
  }
}
