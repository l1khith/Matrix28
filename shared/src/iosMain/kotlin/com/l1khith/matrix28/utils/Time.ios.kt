package com.l1khith.matrix28.utils

import platform.Foundation.NSDate
import platform.Foundation.NSTimeZone
import platform.Foundation.localTimeZone
import platform.Foundation.secondsFromGMTForDate
import platform.Foundation.timeIntervalSince1970

actual fun currentTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}

actual fun getTimeZoneOffset(timestampMs: Long): Long {
    val date = NSDate.dateWithTimeIntervalSince1970(timestampMs / 1000.0)
    return NSTimeZone.localTimeZone.secondsFromGMTForDate(date) * 1000L
}
