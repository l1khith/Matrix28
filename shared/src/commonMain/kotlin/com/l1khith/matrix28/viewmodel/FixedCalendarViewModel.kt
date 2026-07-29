package com.l1khith.matrix28.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.l1khith.matrix28.data.AppTask
import com.l1khith.matrix28.data.RecurrenceType
import com.l1khith.matrix28.data.RecurringTask
import com.l1khith.matrix28.data.TaskDatabase
import com.l1khith.matrix28.utils.FixedCalendarHelper
import com.l1khith.matrix28.utils.FixedDate
import com.l1khith.matrix28.utils.currentTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class FixedCalendarViewModel : ViewModel() {

    private val db = TaskDatabase()

    // Calendar navigation state
    private val _selectedDate = mutableStateOf(FixedCalendarHelper.fromTimestamp(currentTimeMillis()))
    val selectedDate: State<FixedDate> = _selectedDate

    // Task list and indicator state
    private val _tasksForSelectedDay = mutableStateOf<List<AppTask>>(emptyList())
    val tasksForSelectedDay: State<List<AppTask>> = _tasksForSelectedDay

    private val _datesWithActiveTasks = mutableStateOf<Set<String>>(emptySet())
    val datesWithActiveTasks: State<Set<String>> = _datesWithActiveTasks

    // Recurring Tasks state
    private val _recurringTasks = mutableStateOf<List<RecurringTask>>(emptyList())
    val recurringTasks: State<List<RecurringTask>> = _recurringTasks

    init {
        checkAndRunRollover()
        loadState()
    }

    fun selectDate(fixedDate: FixedDate) {
        _selectedDate.value = fixedDate
        loadTasksForSelectedDay()
    }

    fun loadState() {
        loadTasksForSelectedDay()
        loadDatesWithActiveTasks()
        loadRecurringTasks()
    }

    private fun loadTasksForSelectedDay() {
        viewModelScope.launch(Dispatchers.Default) {
            val dateStr = _selectedDate.value.toString()
            generateRecurringInstancesForDate(dateStr)
            val tasks = db.getTasksForDate(dateStr)
            _tasksForSelectedDay.value = tasks
        }
    }

    fun loadDatesWithActiveTasks() {
        viewModelScope.launch(Dispatchers.Default) {
            val dates = db.getDatesWithActiveTasks()
            _datesWithActiveTasks.value = dates
        }
    }

    fun loadRecurringTasks() {
        viewModelScope.launch(Dispatchers.Default) {
            val list = db.getAllRecurringTasks()
            _recurringTasks.value = list
        }
    }

    fun saveTask(
        id: String?,
        title: String,
        description: String?,
        isReminder: Boolean,
        reminderTime: String?,
        priority: Int
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val targetDate = _selectedDate.value
            val isRem = if (isReminder) 1 else 0

            val utcTimestamp = if (isReminder && !reminderTime.isNullOrEmpty()) {
                FixedCalendarHelper.toTimestamp(targetDate, reminderTime)
            } else {
                null
            }

            val task = AppTask(
                id = id ?: Uuid.random().toString(),
                title = title,
                description = description?.ifEmpty { null },
                associatedDate = targetDate.toString(),
                isReminder = isRem,
                reminderTime = if (isReminder) reminderTime else null,
                utcTimestamp = utcTimestamp,
                isCompleted = 0,
                priority = priority,
                recurringParentId = null,
                isGenerated = 0
            )

            if (id != null) {
                com.l1khith.matrix28.utils.cancelTaskAlarm(task)
                db.updateTask(task)
            } else {
                db.insertTask(task)
            }

            if (isReminder) {
                com.l1khith.matrix28.utils.scheduleTaskAlarm(task)
            }

            loadState()
        }
    }

    fun deleteTask(task: AppTask) {
        viewModelScope.launch(Dispatchers.Default) {
            com.l1khith.matrix28.utils.cancelTaskAlarm(task)
            db.deleteTask(task.id)
            loadState()
        }
    }

    fun toggleTaskCompletion(task: AppTask) {
        viewModelScope.launch(Dispatchers.Default) {
            val updatedTask = task.copy(isCompleted = if (task.completed) 0 else 1)
            db.updateTask(updatedTask)

            if (updatedTask.completed) {
                com.l1khith.matrix28.utils.cancelTaskAlarm(updatedTask)
            } else if (updatedTask.reminder) {
                com.l1khith.matrix28.utils.scheduleTaskAlarm(updatedTask)
            }

            loadState()
        }
    }

    // --- Recurring Task Actions ---

    fun saveRecurringTask(
        id: String?,
        title: String,
        description: String?,
        recurrenceType: RecurrenceType,
        recurrenceDays: List<Int>,
        recurrenceInterval: Int,
        priority: Int,
        isActive: Boolean,
        endDate: String?,
        reminderTime: String? = null
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val task = RecurringTask(
                id = id ?: Uuid.random().toString(),
                title = title,
                description = description?.ifEmpty { null },
                recurrenceType = recurrenceType,
                recurrenceDays = recurrenceDays,
                recurrenceInterval = recurrenceInterval,
                priority = priority,
                isActive = isActive,
                createdAt = currentTimeMillis(),
                endDate = endDate?.ifEmpty { null },
                reminderTime = reminderTime?.ifEmpty { null }
            )
            val success = db.insertRecurringTask(task)
            if (success) {
                generateRecurringInstancesForDate(_selectedDate.value.toString())
            }
            loadState()
        }
    }

    fun deleteRecurringTask(id: String) {
        viewModelScope.launch(Dispatchers.Default) {
            db.deleteRecurringTask(id)
            loadState()
        }
    }

    fun toggleRecurringTaskActive(task: RecurringTask) {
        viewModelScope.launch(Dispatchers.Default) {
            val updated = task.copy(isActive = !task.isActive)
            db.insertRecurringTask(updated)

            if (!updated.isActive) {
                db.deleteIncompleteGeneratedTasks(updated.id)
            } else {
                generateRecurringInstancesForDate(_selectedDate.value.toString())
            }

            loadState()
        }
    }

    // --- Instance Generation Engine ---

    fun generateRecurringInstancesForDate(fixedDateStr: String) {
        val fixedDate = FixedCalendarHelper.parseDateStr(fixedDateStr) ?: return
        val dayOfWeek = FixedCalendarHelper.getDayOfWeekIndex(fixedDate)
        
        val activeTasks = db.getActiveRecurringTasks(fixedDateStr)
        for (master in activeTasks) {
            if (db.hasGeneratedInstanceForDate(fixedDateStr, master.id)) {
                continue
            }

            if (shouldGenerateForDate(master, fixedDateStr, dayOfWeek)) {
                val hasReminder = !master.reminderTime.isNullOrEmpty()
                val isReminder = if (hasReminder) 1 else 0
                val utcTimestamp = if (hasReminder) {
                    FixedCalendarHelper.toTimestamp(fixedDate, master.reminderTime)
                } else null

                val generatedTask = AppTask(
                    id = Uuid.random().toString(),
                    title = master.title,
                    description = master.description,
                    associatedDate = fixedDateStr,
                    isReminder = isReminder,
                    reminderTime = master.reminderTime,
                    utcTimestamp = utcTimestamp,
                    isCompleted = 0,
                    priority = master.priority,
                    recurringParentId = master.id,
                    isGenerated = 1
                )
                db.insertGeneratedTask(generatedTask)
                if (hasReminder && generatedTask.reminder) {
                    com.l1khith.matrix28.utils.scheduleTaskAlarm(generatedTask)
                }
            }
        }
    }

    private fun shouldGenerateForDate(master: RecurringTask, dateStr: String, dayOfWeek: Int): Boolean {
        val parts = dateStr.split("-")
        if (parts.size != 3) return false
        val month = parts[1].toIntOrNull() ?: return false
        val day = parts[2].toIntOrNull() ?: return false

        if (master.endDate != null && dateStr > master.endDate) return false
        if (day == 29 && (month == 6 || month == 13)) return false
        if (dayOfWeek == -1) return false

        return when (master.recurrenceType) {
            RecurrenceType.DAILY -> true
            RecurrenceType.WEEKDAYS -> dayOfWeek in 1..5
            RecurrenceType.WEEKENDS -> dayOfWeek == 0 || dayOfWeek == 6
            RecurrenceType.WEEKLY -> master.recurrenceDays.contains(dayOfWeek)
            RecurrenceType.MONTHLY -> day == 1
            RecurrenceType.YEARLY -> month == 1 && day == 1
        }
    }

    fun checkAndRunRollover() {
        val currentDateStr = FixedCalendarHelper.fromTimestamp(currentTimeMillis()).toString()
        db.catchUpRollover(currentDateStr)
    }

    // --- Sync & ICS Actions ---

    fun importSystemCalendar() {
        viewModelScope.launch(Dispatchers.Default) {
            val systemEvents = com.l1khith.matrix28.utils.importSystemCalendarEvents()
            for (task in systemEvents) {
                db.insertTask(task)
            }
            loadState()
        }
    }

    fun getIcsExportString(): String {
        val allTasks = db.getAllTasks()
        return com.l1khith.matrix28.utils.CalendarSyncHelper.exportToIcs(allTasks)
    }

    fun importFromIcsContent(icsContent: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val tasks = com.l1khith.matrix28.utils.CalendarSyncHelper.importFromIcs(icsContent)
            for (task in tasks) {
                db.insertTask(task)
            }
            loadState()
        }
    }
}
