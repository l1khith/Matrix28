package com.l1khith.matrix28.data

actual class TaskDatabase actual constructor() {
    private val tasks = mutableListOf<AppTask>()
    private val recurringTasks = mutableListOf<RecurringTask>()

    actual fun insertTask(task: AppTask): Boolean {
        tasks.removeAll { it.id == task.id }
        return tasks.add(task)
    }

    actual fun updateTask(task: AppTask): Boolean {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            return true
        }
        return false
    }

    actual fun deleteTask(id: String): Boolean {
        return tasks.removeAll { it.id == id }
    }

    actual fun getTasksForDate(dateStr: String): List<AppTask> {
        return tasks.filter { it.dateStr == dateStr }
    }

    actual fun getAllTasks(): List<AppTask> {
        return tasks.toList()
    }

    actual fun getDatesWithActiveTasks(): Set<String> {
        return tasks.filter { !it.isCompleted }.map { it.dateStr }.toSet()
    }

    actual fun insertRecurringTask(task: RecurringTask): Boolean {
        recurringTasks.removeAll { it.id == task.id }
        return recurringTasks.add(task)
    }

    actual fun getAllRecurringTasks(): List<RecurringTask> {
        return recurringTasks.toList()
    }

    actual fun getActiveRecurringTasks(currentDateStr: String): List<RecurringTask> {
        return recurringTasks.filter { task ->
            task.isActive && (task.endDate.isNullOrEmpty() || task.endDate >= currentDateStr)
        }
    }

    actual fun deleteRecurringTask(id: String): Boolean {
        return recurringTasks.removeAll { it.id == id }
    }

    actual fun rolloverTasks(prevDate: String, currDate: String): Int {
        tasks.removeAll { it.associatedDate == prevDate && it.isGenerated == 1 && !it.completed }
        var count = 0
        val uncompletedManual = tasks.filter { it.associatedDate == prevDate && it.isGenerated == 0 && !it.completed }
        tasks.removeAll { it.associatedDate == prevDate && it.isGenerated == 0 && !it.completed }
        uncompletedManual.forEach { task ->
            val rolled = task.copy(associatedDate = currDate)
            tasks.add(rolled)
            count++
        }
        return count
    }

    actual fun catchUpRollover(currDate: String): Int {
        tasks.removeAll { it.associatedDate < currDate && it.isGenerated == 1 && !it.completed }
        var count = 0
        val uncompletedManual = tasks.filter { it.associatedDate < currDate && it.isGenerated == 0 && !it.completed }
        tasks.removeAll { it.associatedDate < currDate && it.isGenerated == 0 && !it.completed }
        uncompletedManual.forEach { task ->
            val rolled = task.copy(associatedDate = currDate)
            tasks.add(rolled)
            count++
        }
        return count
    }

    actual fun insertGeneratedTask(task: AppTask): Boolean {
        return insertTask(task)
    }

    actual fun hasGeneratedInstanceForDate(dateStr: String, parentId: String): Boolean {
        return tasks.any { it.dateStr == dateStr && it.recurringParentId == parentId }
    }

    actual fun deleteIncompleteGeneratedTasks(parentId: String): Int {
        val before = tasks.size
        tasks.removeAll { it.recurringParentId == parentId && !it.isCompleted }
        return before - tasks.size
    }
}
