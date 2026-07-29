package com.l1khith.matrix28.utils

expect fun currentTimeMillis(): Long

data class FixedDate(
    val year: Int,
    val month: Int, // 1 to 13 (Sol is 7)
    val day: Int,   // 1 to 28 (or 29 for Leap Day / Year Day)
    val isLeapDay: Boolean = false,
    val isYearDay: Boolean = false
) {
    override fun toString(): String {
        val monthStr = month.toString().padStart(2, '0')
        val dayStr = day.toString().padStart(2, '0')
        return "$year-$monthStr-$dayStr"
    }
}

object FixedCalendarHelper {

    private val MONTH_NAMES = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "Sol", "July", "August", "September", "October", "November", "December"
    )

    private val WEEKDAYS = arrayOf(
        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    )

    fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    fun getMonthName(monthIndex: Int): String {
        if (monthIndex in 1..13) {
            return MONTH_NAMES[monthIndex - 1]
        }
        return "Unknown"
    }

    /**
     * Converts a Gregorian day of year (1-365/366) to 13-month FixedDate
     */
    fun fromDayOfYear(year: Int, dayOfYear: Int): FixedDate {
        val isLeap = isLeapYear(year)
        return if (isLeap) {
            when {
                dayOfYear == 169 -> FixedDate(year, 6, 29, isLeapDay = true)
                dayOfYear == 366 -> FixedDate(year, 13, 29, isYearDay = true)
                dayOfYear > 169 -> {
                    val shiftedDay = dayOfYear - 1
                    val month = ((shiftedDay - 1) / 28) + 1
                    val day = ((shiftedDay - 1) % 28) + 1
                    FixedDate(year, month, day)
                }
                else -> {
                    val month = ((dayOfYear - 1) / 28) + 1
                    val day = ((dayOfYear - 1) % 28) + 1
                    FixedDate(year, month, day)
                }
            }
        } else {
            if (dayOfYear == 365) {
                FixedDate(year, 13, 29, isYearDay = true)
            } else {
                val month = ((dayOfYear - 1) / 28) + 1
                val day = ((dayOfYear - 1) % 28) + 1
                FixedDate(year, month, day)
            }
        }
    }

    /**
     * Converts a 13-month FixedDate to a Gregorian day of year (1-365/366)
     */
    fun toDayOfYear(fixedDate: FixedDate): Int {
        val isLeap = isLeapYear(fixedDate.year)
        return if (isLeap) {
            when {
                fixedDate.isLeapDay || (fixedDate.month == 6 && fixedDate.day == 29) -> 169
                fixedDate.isYearDay || (fixedDate.month == 13 && fixedDate.day == 29) -> 366
                fixedDate.month > 6 -> {
                    val baseDay = (fixedDate.month - 1) * 28 + fixedDate.day
                    baseDay + 1
                }
                else -> {
                    (fixedDate.month - 1) * 28 + fixedDate.day
                }
            }
        } else {
            if (fixedDate.isYearDay || (fixedDate.month == 13 && fixedDate.day == 29)) {
                365
            } else {
                (fixedDate.month - 1) * 28 + fixedDate.day
            }
        }
    }

    /**
     * Maps a UTC timestamp (milliseconds since epoch) to local FixedDate
     */
    fun fromTimestamp(timestampMs: Long): FixedDate {
        // Approximate calculation based on epoch milliseconds: 86400000 ms per day
        // 1970-01-01 was epoch (Day 1 of 1970).
        val totalDays = (timestampMs / 86400000L).toInt()
        
        var year = 1970
        var remainingDays = totalDays
        
        while (true) {
            val daysInYear = if (isLeapYear(year)) 366 else 365
            if (remainingDays < daysInYear) {
                break
            }
            remainingDays -= daysInYear
            year++
        }
        
        val dayOfYear = remainingDays + 1
        return fromDayOfYear(year, dayOfYear)
    }

    /**
     * Converts local FixedDate and Optional time string ("HH:mm") back to UTC epoch milliseconds
     */
    fun toTimestamp(fixedDate: FixedDate, timeStr: String? = null): Long {
        val dayOfYear = toDayOfYear(fixedDate)
        var daysCount = 0L
        for (y in 1970 until fixedDate.year) {
            daysCount += if (isLeapYear(y)) 366 else 365
        }
        daysCount += (dayOfYear - 1)
        
        var ms = daysCount * 86400000L
        if (timeStr != null && timeStr.contains(":")) {
            val parts = timeStr.split(":")
            val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
            val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
            ms += (h * 3600 + m * 60) * 1000L
        }
        return ms
    }

    /**
     * Gets the weekday name for a FixedDate. Leap Day and Year Day have no weekday designations.
     */
    fun getDayOfWeek(fixedDate: FixedDate): String {
        if (fixedDate.isLeapDay) return "Leap Day"
        if (fixedDate.isYearDay) return "Year Day"
        val index = (fixedDate.day - 1) % 7
        return WEEKDAYS[index]
    }

    /**
     * Gets the weekday index for a FixedDate (0 = Sunday, 6 = Saturday).
     * Returns -1 for Leap Day and Year Day.
     */
    fun getDayOfWeekIndex(fixedDate: FixedDate): Int {
        if (fixedDate.isLeapDay || fixedDate.isYearDay) return -1
        return (fixedDate.day - 1) % 7
    }

    /**
     * Parses a date string in format "YYYY-MM-DD"
     */
    fun parseDateStr(dateStr: String): FixedDate? {
        return try {
            val parts = dateStr.split("-")
            if (parts.size != 3) return null
            val y = parts[0].toInt()
            val m = parts[1].toInt()
            val d = parts[2].toInt()
            val isLeap = isLeapYear(y)
            val isLD = isLeap && m == 6 && d == 29
            val isYD = m == 13 && d == 29
            FixedDate(y, m, d, isLeapDay = isLD, isYearDay = isYD)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Subtracts 1 day from the current date string, returning the yesterday string
     */
    fun getPreviousDateStr(dateStr: String): String {
        val current = parseDateStr(dateStr) ?: return ""
        val (year, month, day) = Triple(current.year, current.month, current.day)
        
        return if (day > 1) {
            val prevDay = day - 1
            FixedDate(year, month, prevDay, isLeapDay = (isLeapYear(year) && month == 6 && prevDay == 29), isYearDay = (month == 13 && prevDay == 29)).toString()
        } else if (month > 1) {
            val prevMonth = month - 1
            val prevDay = if (prevMonth == 6 && isLeapYear(year)) 29 else 28
            FixedDate(year, prevMonth, prevDay, isLeapDay = (isLeapYear(year) && prevMonth == 6 && prevDay == 29), isYearDay = (prevMonth == 13 && prevDay == 29)).toString()
        } else {
            val prevYear = year - 1
            FixedDate(prevYear, 13, 29, isYearDay = true).toString()
        }
    }
}
