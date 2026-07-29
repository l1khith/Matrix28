package com.l1khith.matrix28.data

expect class TaskDatabase() {
    // Regular tasks
    fun insertTask(task: AppTask): Boolean
    fun updateTask(task: AppTask): Boolean
    fun deleteTask(id: String): Boolean
    fun getTasksForDate(dateStr: String): List<AppTask>
    fun getAllTasks(): List<AppTask>
    fun getDatesWithActiveTasks(): Set<String>
    
    // Recurring tasks
    fun insertRecurringTask(task: RecurringTask): Boolean
    fun getAllRecurringTasks(): List<RecurringTask>
    fun getActiveRecurringTasks(currentDateStr: String): List<RecurringTask>
    fun deleteRecurringTask(id: String): Boolean
    
    // Rollover
    fun rolloverTasks(prevDate: String, currDate: String): Int
    fun catchUpRollover(currDate: String): Int
    
    // Instance generation helpers
    fun insertGeneratedTask(task: AppTask): Boolean
    fun hasGeneratedInstanceForDate(dateStr: String, parentId: String): Boolean
    fun deleteIncompleteGeneratedTasks(parentId: String): Int
}
