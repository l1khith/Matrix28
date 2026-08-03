package com.l1khith.matrix28.utils

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import com.l1khith.matrix28.data.AppContext
import com.l1khith.matrix28.data.AppTask
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

actual fun importSystemCalendarEvents(): List<AppTask> {
    val tasks = mutableListOf<AppTask>()
    try {
        val context = AppContext.get()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return emptyList()
        }

        val now = currentTimeMillis()
        val startRange = now - (30L * 24 * 60 * 60 * 1000L) // 30 days ago
        val endRange = now + (365L * 24 * 60 * 60 * 1000L) // 365 days future

        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, startRange)
        ContentUris.appendId(builder, endRange)

        val projection = arrayOf(
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.DESCRIPTION,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.ALL_DAY
        )

        val cursor = context.contentResolver.query(
            builder.build(),
            projection,
            null,
            null,
            "${CalendarContract.Instances.BEGIN} ASC"
        )

        cursor?.use { c ->
            val idIndex = c.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_ID)
            val titleIndex = c.getColumnIndexOrThrow(CalendarContract.Instances.TITLE)
            val descIndex = c.getColumnIndexOrThrow(CalendarContract.Instances.DESCRIPTION)
            val beginIndex = c.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN)
            val allDayIndex = c.getColumnIndexOrThrow(CalendarContract.Instances.ALL_DAY)

            while (c.moveToNext()) {
                val eventId = c.getString(idIndex)
                val title = c.getString(titleIndex) ?: "System Event"
                val description = c.getString(descIndex)
                val startMillis = c.getLong(beginIndex)
                val isAllDay = c.getInt(allDayIndex) == 1

                val fixedDate = FixedCalendarHelper.fromTimestamp(startMillis)
                val associatedDate = fixedDate.toString()

                val isReminder = 0
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
                        id = "sys_${eventId}_${startMillis}",
                        title = title,
                        description = description,
                        associatedDate = associatedDate,
                        isReminder = isReminder,
                        reminderTime = reminderTime,
                        utcTimestamp = null,
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
