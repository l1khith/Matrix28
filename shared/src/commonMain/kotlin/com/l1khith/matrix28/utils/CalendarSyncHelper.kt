package com.l1khith.matrix28.utils

import com.l1khith.matrix28.data.AppTask
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

expect fun importSystemCalendarEvents(): List<AppTask>

object CalendarSyncHelper {

    fun exportToIcs(tasks: List<AppTask>): String {
        val sb = StringBuilder()
        sb.append("BEGIN:VCALENDAR\r\n")
        sb.append("VERSION:2.0\r\n")
        sb.append("PRODID:-//FixedCalendarPlanner//NONSGML v1.0//EN\r\n")

        for (task in tasks) {
            sb.append("BEGIN:VEVENT\r\n")
            sb.append("UID:${task.id}\r\n")

            val timestamp = if (task.isReminder == 1 && task.utcTimestamp != null) {
                task.utcTimestamp
            } else {
                val fixedDate = FixedCalendarHelper.parseDateStr(task.associatedDate)
                if (fixedDate != null) {
                    FixedCalendarHelper.toTimestamp(fixedDate, "00:00")
                } else {
                    currentTimeMillis()
                }
            }

            val utcStr = formatUtcIcs(timestamp)
            sb.append("DTSTART:$utcStr\r\n")
            sb.append("SUMMARY:${task.title.replace("\n", " ").replace(",", "\\,")}\r\n")
            if (!task.description.isNullOrEmpty()) {
                sb.append("DESCRIPTION:${task.description.replace("\n", "\\n").replace(",", "\\,")}\r\n")
            }
            sb.append("END:VEVENT\r\n")
        }

        sb.append("END:VCALENDAR\r\n")
        return sb.toString()
    }

    @OptIn(ExperimentalUuidApi::class)
    fun importFromIcs(icsContent: String): List<AppTask> {
        val tasks = mutableListOf<AppTask>()
        val lines = icsContent.lines()
        var currentTitle = ""
        var currentDesc = ""
        var currentUid = ""
        var currentFixedDate: FixedDate = FixedCalendarHelper.fromTimestamp(currentTimeMillis())
        var currentTimestamp: Long? = null
        var inEvent = false

        val isNative13Month = icsContent.contains("X-13MONTH-CALENDAR") || icsContent.contains("PRODID:-//FixedCalendarPlanner")

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed == "BEGIN:VEVENT") {
                inEvent = true
                currentTitle = "Untitled Event"
                currentDesc = ""
                currentUid = Uuid.random().toString()
                currentFixedDate = FixedCalendarHelper.fromTimestamp(currentTimeMillis())
                currentTimestamp = null
            } else if (trimmed == "END:VEVENT") {
                if (inEvent) {
                    val isReminder = 0
                    val reminderTime: String? = null

                    tasks.add(
                        AppTask(
                            id = "ics_$currentUid",
                            title = currentTitle,
                            description = currentDesc.ifEmpty { null },
                            associatedDate = currentFixedDate.toString(),
                            isReminder = isReminder,
                            reminderTime = reminderTime,
                            utcTimestamp = currentTimestamp,
                            isCompleted = 0,
                            priority = 1
                        )
                    )
                    inEvent = false
                }
            } else if (inEvent) {
                val colonIndex = trimmed.indexOf(':')
                if (colonIndex != -1) {
                    val key = trimmed.substring(0, colonIndex)
                    val value = trimmed.substring(colonIndex + 1)
                    when {
                        key.startsWith("SUMMARY") -> currentTitle = value.replace("\\,", ",")
                        key.startsWith("DESCRIPTION") -> currentDesc = value.replace("\\n", "\n").replace("\\,", ",")
                        key.startsWith("UID") -> currentUid = value
                        key.startsWith("DTSTART") -> {
                            val cleanVal = value.replace(";", "").replace(":", "")
                            val parsed = parseIcsDtStart(cleanVal, isNative13Month)
                            currentFixedDate = parsed.first
                            currentTimestamp = parsed.second
                        }
                    }
                }
            }
        }
        return tasks
    }

    private fun formatUtcIcs(timestampMs: Long): String {
        val totalSeconds = timestampMs / 1000L
        val seconds = (totalSeconds % 60).toInt()
        val totalMinutes = totalSeconds / 60L
        val minutes = (totalMinutes % 60).toInt()
        val totalHours = totalMinutes / 60L
        val hours = (totalHours % 24).toInt()

        val fixedDate = FixedCalendarHelper.fromTimestamp(timestampMs)
        val y = fixedDate.year.toString().padStart(4, '0')
        val m = fixedDate.month.toString().padStart(2, '0')
        val d = fixedDate.day.toString().padStart(2, '0')
        val h = hours.toString().padStart(2, '0')
        val min = minutes.toString().padStart(2, '0')
        val s = seconds.toString().padStart(2, '0')
        return "${y}${m}${d}T${h}${min}${s}Z"
    }

    private fun parseIcsDtStart(value: String, isNative13Month: Boolean): Pair<FixedDate, Long?> {
        val now = currentTimeMillis()
        val defaultDate = FixedCalendarHelper.fromTimestamp(now)
        return try {
            val datePart = value.take(8)
            if (datePart.length == 8) {
                val y = datePart.substring(0, 4).toInt()
                val m = datePart.substring(4, 6).toInt()
                val d = datePart.substring(6, 8).toInt()

                val timePart = if (value.contains("T")) value.substringAfter("T").take(6) else "000000"
                val h = timePart.substring(0, 2).toIntOrNull() ?: 0
                val min = timePart.substring(2, 4).toIntOrNull() ?: 0
                val timeStr = "$h:$min"

                if (isNative13Month) {
                    val fixedDate = FixedDate(y, m.coerceIn(1, 13), d.coerceIn(1, 29))
                    val ts = FixedCalendarHelper.toTimestamp(fixedDate, timeStr)
                    Pair(fixedDate, ts)
                } else {
                    // Standard 12-month Gregorian date -> convert to 13-month FixedDate
                    val fixedDate = FixedCalendarHelper.gregorianToFixed(y, m, d)
                    val ts = FixedCalendarHelper.toTimestamp(fixedDate, timeStr)
                    Pair(fixedDate, ts)
                }
            } else {
                Pair(defaultDate, now)
            }
        } catch (e: Exception) {
            Pair(defaultDate, now)
        }
    }
}
