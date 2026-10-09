package com.example.data

import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CalendarEvent(
    val title: String,
    val time: String,
    val isToday: Boolean
)

class CalendarRepository(private val context: Context) {

    fun getNextEvent(): CalendarEvent? {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CALENDAR)
            != PackageManager.PERMISSION_GRANTED) {
            return null
        }

        val now = System.currentTimeMillis()
        val end = now + 48 * 60 * 60 * 1000 // 48 hours ahead

        val builder: Uri.Builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, now)
        ContentUris.appendId(builder, end)

        val projection = arrayOf(
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.ALL_DAY
        )

        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(
                builder.build(),
                projection,
                null,
                null,
                "${CalendarContract.Instances.BEGIN} ASC"
            )

            if (cursor != null && cursor.moveToFirst()) {
                val titleIdx = cursor.getColumnIndex(CalendarContract.Instances.TITLE)
                val beginIdx = cursor.getColumnIndex(CalendarContract.Instances.BEGIN)
                val allDayIdx = cursor.getColumnIndex(CalendarContract.Instances.ALL_DAY)

                val title = cursor.getString(titleIdx) ?: "Event"
                val begin = cursor.getLong(beginIdx)
                val isAllDay = cursor.getInt(allDayIdx) == 1

                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val eventDateStr = dateFormat.format(Date(begin))
                val todayStr = dateFormat.format(Date(now))
                val isToday = eventDateStr == todayStr

                val timeStr = if (isAllDay) "ALL DAY" else timeFormat.format(Date(begin))

                return CalendarEvent(
                    title = title,
                    time = timeStr,
                    isToday = isToday
                )
            }
        } catch (_: Exception) {
        } finally {
            cursor?.close()
        }

        return null
    }
}
