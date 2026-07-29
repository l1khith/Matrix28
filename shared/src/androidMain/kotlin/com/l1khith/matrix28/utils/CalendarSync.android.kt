package com.l1khith.matrix28.utils

import android.provider.CalendarContract
import com.l1khith.matrix28.data.AppContext
import com.l1khith.matrix28.data.AppTask
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

actual fun importSystemCalendarEvents(): List<AppTask> {
    val tasks = mutableListOf<AppTask>()
    try {
        val context = AppContext.get()
        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.ALL_DAY
        )

        val now = currentTimeMillis()
        val startRange = now - (30L * 24 * 60 * 60 * 1000L) // 30 days ago
        val endRange = now + (365L * 24 * 60 * 60 * 1000L) // 365 days future

        val selection = "${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} <= ?"
        val selectionArgs = arrayOf(startRange.toString(), endRange.toString())

        val cursor = context.contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${CalendarContract.Events.DTSTART} ASC"
        )

        cursor?.use { c ->
            val idIndex = c.getColumnIndexOrThrow(CalendarContract.Events._ID)
            val titleIndex = c.getColumnIndexOrThrow(CalendarContract.Events.TITLE)
            val descIndex = c.getColumnIndexOrThrow(CalendarContract.Events.DESCRIPTION)
            val startIndex = c.getColumnIndexOrThrow(CalendarContract.Events.DTSTART)
            val allDayIndex = c.getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY)

            while (c.moveToNext()) {
                val eventId = c.getString(idIndex)
                val title = c.getString(titleIndex) ?: "System Event"
                val description = c.getString(descIndex)
                val startMillis = c.getLong(startIndex)
                val isAllDay = c.getInt(allDayIndex) == 1

                val fixedDate = FixedCalendarHelper.fromTimestamp(startMillis)
                val associatedDate = fixedDate.toString()

                val isReminder = if (isAllDay) 0 else 1
                val reminderTime = if (isAllDay) {
                    null
                } else {
                    val localDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startMillis), ZoneId.systemDefault())
                    val h = localDateTime.hour.toString().padStart(2, '0')
                    val m = localDateTime.minute.toString().padStart(2, '0')
                    "$h:$m"
                }

                tasks.add(
                    AppTask(
                        id = "sys_$eventId",
                        title = title,
                        description = description,
                        associatedDate = associatedDate,
                        isReminder = isReminder,
                        reminderTime = reminderTime,
                        utcTimestamp = if (isReminder == 1) startMillis else null,
                        isCompleted = 0,
                        priority = 1
                    )
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return tasks
}
