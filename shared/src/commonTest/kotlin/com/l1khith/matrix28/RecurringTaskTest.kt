package com.l1khith.matrix28

import com.l1khith.matrix28.data.AppTask
import com.l1khith.matrix28.data.RecurrenceType
import com.l1khith.matrix28.data.TaskDatabase
import com.l1khith.matrix28.utils.CalendarSyncHelper
import com.l1khith.matrix28.utils.FixedCalendarHelper
import com.l1khith.matrix28.utils.FixedDate
import com.l1khith.matrix28.viewmodel.FixedCalendarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RecurringTaskTest {

    @Test
    fun testFixedCalendarHelperMath() {
        val Jan1 = FixedDate(2026, 1, 1)
        assertEquals("2026-01-01", Jan1.toString())
        assertEquals("January", FixedCalendarHelper.getMonthName(1))
        assertEquals("Sol", FixedCalendarHelper.getMonthName(7))
        assertEquals("December", FixedCalendarHelper.getMonthName(13))

        // Weekday Index: Sun = 0, Mon = 1, Sat = 6
        assertEquals(0, FixedCalendarHelper.getDayOfWeekIndex(Jan1))
        assertEquals(1, FixedCalendarHelper.getDayOfWeekIndex(FixedDate(2026, 1, 2)))
        assertEquals(6, FixedCalendarHelper.getDayOfWeekIndex(FixedDate(2026, 1, 7)))

        // Leap Day and Year Day: weekday index = -1
        val leapDay = FixedDate(2024, 6, 29, isLeapDay = true)
        val yearDay = FixedDate(2026, 13, 29, isYearDay = true)
        assertEquals(-1, FixedCalendarHelper.getDayOfWeekIndex(leapDay))
        assertEquals(-1, FixedCalendarHelper.getDayOfWeekIndex(yearDay))

        // Previous date calculation
        assertEquals("2026-01-01", FixedCalendarHelper.getPreviousDateStr("2026-01-02"))
        assertEquals("2025-13-29", FixedCalendarHelper.getPreviousDateStr("2026-01-01"))
    }

    @Test
    fun testTaskDatabaseCRUD() {
        val db = TaskDatabase()
        val task = AppTask(
            id = "test_1",
            title = "Buy Milk",
            description = "Groceries",
            associatedDate = "2026-07-13",
            isReminder = 0,
            reminderTime = null,
            utcTimestamp = null,
            isCompleted = 0,
            priority = 2
        )

        val inserted = db.insertTask(task)
        assertTrue(inserted)

        val tasks = db.getTasksForDate("2026-07-13")
        assertEquals(1, tasks.size)
        assertEquals("Buy Milk", tasks[0].title)

        val activeDates = db.getDatesWithActiveTasks()
        assertTrue(activeDates.contains("2026-07-13"))

        // Update task completion
        val completedTask = task.copy(isCompleted = 1)
        db.updateTask(completedTask)
        val updatedTasks = db.getTasksForDate("2026-07-13")
        assertTrue(updatedTasks[0].completed)

        // Delete task
        val deleted = db.deleteTask("test_1")
        assertTrue(deleted)
        assertEquals(0, db.getTasksForDate("2026-07-13").size)
    }

    @Test
    fun testRecurringTaskEngine() = runBlocking {
        val vm = FixedCalendarViewModel()
        vm.selectDate(FixedDate(2026, 7, 13))

        // Create a DAILY recurring task template
        vm.saveRecurringTask(
            id = "rec_daily",
            title = "Morning Exercise",
            description = "30 mins cardio",
            recurrenceType = RecurrenceType.DAILY,
            recurrenceDays = emptyList(),
            recurrenceInterval = 1,
            priority = 3,
            isActive = true,
            endDate = null
        )

        // Wait for coroutine completion
        withContext(Dispatchers.Default) {
            kotlinx.coroutines.delay(100)
        }

        val recurringList = vm.recurringTasks.value
        assertTrue(recurringList.any { it.id == "rec_daily" && it.title == "Morning Exercise" })

        // Check that a generated instance appears in tasks for selected day
        val dayTasks = vm.tasksForSelectedDay.value
        val generated = dayTasks.firstOrNull { it.recurringParentId == "rec_daily" }
        assertNotNull(generated)
        assertEquals(1, generated.isGenerated)
        assertEquals("Morning Exercise", generated.title)

        // Toggle recurring task inactive
        val recTemplate = recurringList.first { it.id == "rec_daily" }
        vm.toggleRecurringTaskActive(recTemplate)

        withContext(Dispatchers.Default) {
            kotlinx.coroutines.delay(100)
        }

        // Verify template is inactive and generated incomplete task is removed
        val updatedDayTasks = vm.tasksForSelectedDay.value
        assertFalse(updatedDayTasks.any { it.recurringParentId == "rec_daily" })

        // Clean up
        vm.deleteRecurringTask("rec_daily")
    }

    @Test
    fun testIcsExportAndImport() {
        val task = AppTask(
            id = "ics_test",
            title = "Team Sync",
            description = "Weekly status update",
            associatedDate = "2026-07-13",
            isReminder = 1,
            reminderTime = "14:30",
            utcTimestamp = 1783953000000L,
            isCompleted = 0,
            priority = 2
        )

        val icsString = CalendarSyncHelper.exportToIcs(listOf(task))
        assertTrue(icsString.contains("BEGIN:VCALENDAR"))
        assertTrue(icsString.contains("SUMMARY:Team Sync"))
        assertTrue(icsString.contains("DESCRIPTION:Weekly status update"))
        assertTrue(icsString.contains("END:VCALENDAR"))

        val importedTasks = CalendarSyncHelper.importFromIcs(icsString)
        assertTrue(importedTasks.isNotEmpty())
        assertEquals("Team Sync", importedTasks[0].title)
    }
}
