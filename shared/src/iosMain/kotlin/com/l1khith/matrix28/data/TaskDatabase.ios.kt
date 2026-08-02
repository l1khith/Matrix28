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
        return recurringTasks.toList()
    }

    actual fun deleteRecurringTask(id: String): Boolean {
        return recurringTasks.removeAll { it.id == id }
    }

    actual fun rolloverTasks(prevDate: String, currDate: String): Int {
        var count = 0
        val uncompleted = tasks.filter { it.dateStr == prevDate && !it.isCompleted }
        uncompleted.forEach { task ->
            val rolled = task.copy(id = task.id + "_rolled_" + currDate, dateStr = currDate)
            tasks.add(rolled)
            count++
        }
        return count
    }

    actual fun catchUpRollover(currDate: String): Int {
        return 0
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
