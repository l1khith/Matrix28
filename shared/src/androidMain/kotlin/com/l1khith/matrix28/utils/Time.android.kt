package com.l1khith.matrix28.utils

import java.util.TimeZone

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

actual fun getTimeZoneOffset(timestampMs: Long): Long {
    return TimeZone.getDefault().getOffset(timestampMs).toLong()
}
