package com.weatherapp.utils


import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object TimeUtils {

    fun getCurrentTimeForTimezone(timezoneOffset: Int): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.add(Calendar.SECOND, timezoneOffset)

        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")

        return format.format(calendar.time)
    }

    fun getCurrentDateForTimezone(timezoneOffset: Int): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.add(Calendar.SECOND, timezoneOffset)

        val format = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")

        return format.format(calendar.time)
    }

    fun getRelativeTimeString(timestamp: Date): String {
        val now = Date()
        val diffMinutes = (now.time - timestamp.time) / (60 * 1000)

        return when {
            diffMinutes < 1 -> "Just now"
            diffMinutes == 1L -> "1 minute ago"
            diffMinutes < 60 -> "$diffMinutes minutes ago"
            diffMinutes < 120 -> "1 hour ago"
            diffMinutes < 1440 -> "${diffMinutes / 60} hours ago"
            else -> SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault()).format(timestamp)
        }
    }
}
