package com.l1khith.matrix28.data

import java.util.concurrent.ConcurrentHashMap

actual class TaskDatabase actual constructor() {
    private val tasksMap = ConcurrentHashMap<String, AppTask>()
    private val recurringMap = ConcurrentHashMap<String, RecurringTask>()

    actual fun insertTask(task: AppTask): Boolean {
        tasksMap[task.id] = task
        return true
    }

    actual fun updateTask(task: AppTask): Boolean {
        tasksMap[task.id] = task
        return true
    }

    actual fun deleteTask(id: String): Boolean {
        return tasksMap.remove(id) != null
    }

    actual fun getTasksForDate(dateStr: String): List<AppTask> {
        return tasksMap.values
            .filter { it.associatedDate == dateStr }
            .sortedWith(compareBy({ it.isCompleted }, { -it.priority }))
    }

    actual fun getAllTasks(): List<AppTask> {
        return tasksMap.values.toList()
    }

    actual fun getDatesWithActiveTasks(): Set<String> {
        return tasksMap.values
            .filter { it.isCompleted == 0 }
            .map { it.associatedDate }
            .toSet()
    }

    actual fun insertRecurringTask(task: RecurringTask): Boolean {
        recurringMap[task.id] = task
        return true
    }

    actual fun getAllRecurringTasks(): List<RecurringTask> {
        return recurringMap.values.sortedByDescending { it.createdAt }
    }

    actual fun getActiveRecurringTasks(currentDateStr: String): List<RecurringTask> {
        return recurringMap.values.filter { master ->
            master.isActive && (master.endDate == null || master.endDate >= currentDateStr)
        }
    }

    actual fun deleteRecurringTask(id: String): Boolean {
        deleteIncompleteGeneratedTasks(id)
        return recurringMap.remove(id) != null
    }

    actual fun rolloverTasks(prevDate: String, currDate: String): Int {
        var count = 0
        for ((id, task) in tasksMap) {
            if (task.associatedDate == prevDate && task.isCompleted == 0 && task.isReminder == 0) {
                tasksMap[id] = task.copy(associatedDate = currDate)
                count++
            }
        }
        return count
    }

    actual fun catchUpRollover(currDate: String): Int {
        var count = 0
        for ((id, task) in tasksMap) {
            if (task.associatedDate < currDate && task.isCompleted == 0 && task.isReminder == 0) {
                tasksMap[id] = task.copy(associatedDate = currDate)
                count++
            }
        }
        return count
    }

    actual fun insertGeneratedTask(task: AppTask): Boolean {
        return insertTask(task)
    }

    actual fun hasGeneratedInstanceForDate(dateStr: String, parentId: String): Boolean {
        return tasksMap.values.any { it.associatedDate == dateStr && it.recurringParentId == parentId }
    }

    actual fun deleteIncompleteGeneratedTasks(parentId: String): Int {
        val toRemove = tasksMap.values.filter { it.recurringParentId == parentId && it.isCompleted == 0 }
        var count = 0
        for (task in toRemove) {
            if (tasksMap.remove(task.id) != null) {
                count++
            }
        }
        return count
    }
}
