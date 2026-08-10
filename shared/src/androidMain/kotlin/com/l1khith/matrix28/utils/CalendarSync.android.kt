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
        // Defined import range limits: Past 30 days to Next 365 days
        val startRange = now - (30L * 24 * 60 * 60 * 1000L)
        val endRange = now + (365L * 24 * 60 * 60 * 1000L)

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

        val todayStr = FixedCalendarHelper.fromTimestamp(now).toString()

        cursor?.use { c ->
            val titleIndex = c.getColumnIndexOrThrow(CalendarContract.Instances.TITLE)
            val descIndex = c.getColumnIndexOrThrow(CalendarContract.Instances.DESCRIPTION)
            val beginIndex = c.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN)
            val allDayIndex = c.getColumnIndexOrThrow(CalendarContract.Instances.ALL_DAY)

            while (c.moveToNext()) {
                val title = c.getString(titleIndex) ?: "System Event"
                val description = c.getString(descIndex)
                val startMillis = c.getLong(beginIndex)
                val isAllDay = c.getInt(allDayIndex) == 1

                val fixedDate = FixedCalendarHelper.fromTimestamp(startMillis)
                val associatedDate = fixedDate.toString()

                if (associatedDate < todayStr) {
                    continue
                }

                val reminderTime = if (isAllDay) {
                    null
                } else {
                    val localDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startMillis), ZoneId.systemDefault())
                    val h = localDateTime.hour.toString().padStart(2, '0')
                    val m = localDateTime.minute.toString().padStart(2, '0')
                    "$h:$m"
                }

                // Deterministic Unique Task ID generation based on normalized title and date
                val cleanTitle = title.trim().lowercase()
                val hashId = (cleanTitle + "_" + associatedDate).hashCode().let { if (it < 0) -it else it }
                val deterministicId = "sys_${hashId}"

                tasks.add(
                    AppTask(
                        id = deterministicId,
                        title = title.trim(),
                        description = description,
                        associatedDate = associatedDate,
                        isReminder = 0,
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
    // Deduplicate in memory before returning
    return tasks.distinctBy { "${it.title.trim().lowercase()}_${it.associatedDate}" }
}
