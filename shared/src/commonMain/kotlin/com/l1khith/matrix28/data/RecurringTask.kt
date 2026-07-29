package com.l1khith.matrix28.data

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

enum class RecurrenceType {
    DAILY, WEEKDAYS, WEEKENDS, WEEKLY, MONTHLY, YEARLY
}

@OptIn(ExperimentalUuidApi::class)
data class RecurringTask(
    val id: String = Uuid.random().toString(),
    val title: String,
    val description: String? = null,
    val recurrenceType: RecurrenceType,
    val recurrenceDays: List<Int> = emptyList(), // 0 = Sun, 6 = Sat
    val recurrenceInterval: Int = 1,
    val priority: Int = 1,
    val isActive: Boolean = true,
    val createdAt: Long = 0L,
    val endDate: String? = null,
    val reminderTime: String? = null
)
