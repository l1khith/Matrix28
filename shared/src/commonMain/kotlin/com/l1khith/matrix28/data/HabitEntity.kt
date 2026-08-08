package com.l1khith.matrix28.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String,
    val reminderTime: String? = null,
    val isPaused: Boolean = false,
    val createdAtEpochDay: Long
)

@Entity(
    tableName = "habit_entries",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habitId"]),
        Index(value = ["habitId", "cycleIndex"]),
        Index(value = ["habitId", "cycleIndex", "dayInCycle"], unique = true)
    ]
)
data class HabitEntryEntity(
    @PrimaryKey
    val id: String,
    val habitId: String,
    val cycleIndex: Long,
    val dayInCycle: Int,
    val epochDay: Long,
    val isCompleted: Boolean,
    val completedAtMs: Long? = null
)
