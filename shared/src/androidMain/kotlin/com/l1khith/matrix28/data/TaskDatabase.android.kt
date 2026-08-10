package com.l1khith.matrix28.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.l1khith.matrix28.utils.FixedCalendarHelper


object AppContext {
    @Volatile
    private var context: Context? = null
    fun init(ctx: Context) {
        context = ctx.applicationContext
    }
    fun get(): Context {
        val current = context
        if (current != null) return current
        return try {
            val providerClass = Class.forName("androidx.test.core.app.ApplicationProvider")
            val method = providerClass.getMethod("getApplicationContext")
            val testCtx = method.invoke(null) as Context
            context = testCtx
            testCtx
        } catch (_: Throwable) {
            throw IllegalStateException("AppContext not initialized. Call AppContext.init(context) in Application or MainActivity.")
        }
    }
}

actual class TaskDatabase actual constructor() {

    private class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE $TABLE_TASKS (
                    $COLUMN_ID TEXT PRIMARY KEY,
                    $COLUMN_TITLE TEXT NOT NULL,
                    $COLUMN_DESCRIPTION TEXT,
                    $COLUMN_ASSOCIATED_DATE TEXT NOT NULL,
                    $COLUMN_IS_REMINDER INTEGER NOT NULL DEFAULT 0,
                    $COLUMN_REMINDER_TIME TEXT,
                    $COLUMN_UTC_TIMESTAMP INTEGER,
                    $COLUMN_IS_COMPLETED INTEGER NOT NULL DEFAULT 0,
                    $COLUMN_PRIORITY INTEGER NOT NULL DEFAULT 1,
                    $COLUMN_RECURRING_PARENT_ID TEXT,
                    $COLUMN_IS_GENERATED INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE $TABLE_RECURRING (
                    $COLUMN_REC_ID TEXT PRIMARY KEY,
                    $COLUMN_REC_TITLE TEXT NOT NULL,
                    $COLUMN_REC_DESCRIPTION TEXT,
                    $COLUMN_REC_TYPE TEXT NOT NULL,
                    $COLUMN_REC_DAYS TEXT,
                    $COLUMN_REC_INTERVAL INTEGER NOT NULL DEFAULT 1,
                    $COLUMN_REC_PRIORITY INTEGER NOT NULL DEFAULT 1,
                    $COLUMN_REC_IS_ACTIVE INTEGER NOT NULL DEFAULT 1,
                    $COLUMN_REC_CREATED_AT INTEGER NOT NULL,
                    $COLUMN_REC_END_DATE TEXT,
                    $COLUMN_REC_REMINDER_TIME TEXT
                )
                """.trimIndent()
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            if (oldVersion < 2) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS $TABLE_RECURRING (
                        $COLUMN_REC_ID TEXT PRIMARY KEY,
                        $COLUMN_REC_TITLE TEXT NOT NULL,
                        $COLUMN_REC_DESCRIPTION TEXT,
                        $COLUMN_REC_TYPE TEXT NOT NULL,
                        $COLUMN_REC_DAYS TEXT,
                        $COLUMN_REC_INTERVAL INTEGER NOT NULL DEFAULT 1,
                        $COLUMN_REC_PRIORITY INTEGER NOT NULL DEFAULT 1,
                        $COLUMN_REC_IS_ACTIVE INTEGER NOT NULL DEFAULT 1,
                        $COLUMN_REC_CREATED_AT INTEGER NOT NULL,
                        $COLUMN_REC_END_DATE TEXT,
                        $COLUMN_REC_REMINDER_TIME TEXT
                    )
                    """.trimIndent()
                )
                try {
                    db.execSQL("ALTER TABLE $TABLE_TASKS ADD COLUMN $COLUMN_RECURRING_PARENT_ID TEXT")
                    db.execSQL("ALTER TABLE $TABLE_TASKS ADD COLUMN $COLUMN_IS_GENERATED INTEGER NOT NULL DEFAULT 0")
                    db.execSQL("ALTER TABLE $TABLE_RECURRING ADD COLUMN $COLUMN_REC_REMINDER_TIME TEXT")
                } catch (_: Exception) {}
            }
        }
    }

    private val helper: DbHelper by lazy { DbHelper(AppContext.get()) }

    private fun notifyWidgetUpdate() {
        try {
            com.l1khith.matrix28.widget.WidgetUpdater.updateWidget()
        } catch (_: Exception) {}
    }

    init {
        purgeDuplicateTasks()
    }

    fun purgeDuplicateTasks() {
        try {
            val db = helper.writableDatabase
            db.execSQL(
                """
                DELETE FROM $TABLE_TASKS 
                WHERE rowid NOT IN (
                    SELECT MIN(rowid) 
                    FROM $TABLE_TASKS 
                    GROUP BY LOWER(TRIM($COLUMN_TITLE)), $COLUMN_ASSOCIATED_DATE
                )
                """.trimIndent()
            )
        } catch (_: Exception) {}
    }

    actual fun insertTask(task: AppTask): Boolean {
        val db = helper.writableDatabase

        if (task.id.startsWith("sys_")) {
            val existingCursor = db.query(
                TABLE_TASKS,
                arrayOf(COLUMN_ID),
                "LOWER(TRIM($COLUMN_TITLE)) = LOWER(TRIM(?)) AND $COLUMN_ASSOCIATED_DATE = ?",
                arrayOf(task.title, task.associatedDate),
                null, null, null
            )
            existingCursor?.use { c ->
                if (c.moveToFirst()) {
                    return false
                }
            }
        }

        val values = ContentValues().apply {
            put(COLUMN_ID, task.id)
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_ASSOCIATED_DATE, task.associatedDate)
            put(COLUMN_IS_REMINDER, task.isReminder)
            put(COLUMN_REMINDER_TIME, task.reminderTime)
            put(COLUMN_UTC_TIMESTAMP, task.utcTimestamp)
            put(COLUMN_IS_COMPLETED, task.isCompleted)
            put(COLUMN_PRIORITY, task.priority)
            put(COLUMN_RECURRING_PARENT_ID, task.recurringParentId)
            put(COLUMN_IS_GENERATED, task.isGenerated)
        }
        val result = db.insertWithOnConflict(TABLE_TASKS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        if (result != -1L) {
            notifyWidgetUpdate()
            return true
        }
        return false
    }


    actual fun updateTask(task: AppTask): Boolean {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_ASSOCIATED_DATE, task.associatedDate)
            put(COLUMN_IS_REMINDER, task.isReminder)
            put(COLUMN_REMINDER_TIME, task.reminderTime)
            put(COLUMN_UTC_TIMESTAMP, task.utcTimestamp)
            put(COLUMN_IS_COMPLETED, task.isCompleted)
            put(COLUMN_PRIORITY, task.priority)
            put(COLUMN_RECURRING_PARENT_ID, task.recurringParentId)
            put(COLUMN_IS_GENERATED, task.isGenerated)
        }
        val result = db.update(TABLE_TASKS, values, "$COLUMN_ID = ?", arrayOf(task.id))
        if (result > 0) {
            notifyWidgetUpdate()
            return true
        }
        return false
    }

    actual fun deleteTask(id: String): Boolean {
        val db = helper.writableDatabase
        val result = db.delete(TABLE_TASKS, "$COLUMN_ID = ?", arrayOf(id))
        if (result > 0) {
            notifyWidgetUpdate()
            return true
        }
        return false
    }


    actual fun getTasksForDate(dateStr: String): List<AppTask> {
        val list = mutableListOf<AppTask>()
        val db = helper.readableDatabase
        val cursor = db.query(
            TABLE_TASKS, null, "$COLUMN_ASSOCIATED_DATE = ?", arrayOf(dateStr),
            null, null, "$COLUMN_PRIORITY DESC, $COLUMN_IS_REMINDER DESC, $COLUMN_REMINDER_TIME ASC"
        )
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(cursorToTask(c))
            }
        }
        return list
    }

    actual fun getAllTasks(): List<AppTask> {
        val list = mutableListOf<AppTask>()
        val db = helper.readableDatabase
        val cursor = db.query(
            TABLE_TASKS, null, null, null, null, null, null
        )
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(cursorToTask(c))
            }
        }
        return list
    }

    actual fun getDatesWithActiveTasks(): Set<String> {
        val set = mutableSetOf<String>()
        val db = helper.readableDatabase
        val cursor = db.query(TABLE_TASKS, arrayOf(COLUMN_ASSOCIATED_DATE), "$COLUMN_IS_COMPLETED = 0", null, COLUMN_ASSOCIATED_DATE, null, null)
        cursor.use { c ->
            while (c.moveToNext()) {
                set.add(c.getString(0))
            }
        }
        return set
    }

    actual fun getTaskCountsPerDate(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        val db = helper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_ASSOCIATED_DATE, COUNT(*) FROM $TABLE_TASKS WHERE $COLUMN_IS_COMPLETED = 0 GROUP BY $COLUMN_ASSOCIATED_DATE",
            null
        )
        cursor.use { c ->
            while (c.moveToNext()) {
                val date = c.getString(0)
                val count = c.getInt(1)
                map[date] = count
            }
        }
        return map
    }

    actual fun insertRecurringTask(task: RecurringTask): Boolean {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_REC_ID, task.id)
            put(COLUMN_REC_TITLE, task.title)
            put(COLUMN_REC_DESCRIPTION, task.description)
            put(COLUMN_REC_TYPE, task.recurrenceType.name)
            put(COLUMN_REC_DAYS, task.recurrenceDays.joinToString(","))
            put(COLUMN_REC_INTERVAL, task.recurrenceInterval)
            put(COLUMN_REC_PRIORITY, task.priority)
            put(COLUMN_REC_IS_ACTIVE, if (task.isActive) 1 else 0)
            put(COLUMN_REC_CREATED_AT, task.createdAt)
            put(COLUMN_REC_END_DATE, task.endDate)
            put(COLUMN_REC_REMINDER_TIME, task.reminderTime)
        }
        val result = db.insertWithOnConflict(TABLE_RECURRING, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        return result != -1L
    }

    actual fun getAllRecurringTasks(): List<RecurringTask> {
        val list = mutableListOf<RecurringTask>()
        val db = helper.readableDatabase
        val cursor = db.query(TABLE_RECURRING, null, null, null, null, null, "$COLUMN_REC_CREATED_AT DESC")
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(cursorToRecurringTask(c))
            }
        }
        return list
    }

    actual fun getActiveRecurringTasks(currentDateStr: String): List<RecurringTask> {
        val list = mutableListOf<RecurringTask>()
        val db = helper.readableDatabase
        val cursor = db.query(
            TABLE_RECURRING, null,
            "$COLUMN_REC_IS_ACTIVE = 1 AND ($COLUMN_REC_END_DATE IS NULL OR $COLUMN_REC_END_DATE = '' OR $COLUMN_REC_END_DATE >= ?)",
            arrayOf(currentDateStr), null, null, null
        )
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(cursorToRecurringTask(c))
            }
        }
        return list
    }

    actual fun deleteRecurringTask(id: String): Boolean {
        val db = helper.writableDatabase
        db.beginTransaction()
        var success = false
        try {
            db.delete(TABLE_TASKS, "$COLUMN_RECURRING_PARENT_ID = ? AND $COLUMN_IS_COMPLETED = 0", arrayOf(id))
            val result = db.delete(TABLE_RECURRING, "$COLUMN_REC_ID = ?", arrayOf(id))
            success = result > 0
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return success
    }

    actual fun rolloverTasks(prevDate: String, currDate: String): Int {
        return 0
    }

    actual fun catchUpRollover(currDate: String): Int {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            // Delete all past uncompleted generated recurring instances so routines never pile up
            db.delete(TABLE_TASKS, "$COLUMN_ASSOCIATED_DATE < ? AND $COLUMN_IS_GENERATED = 1 AND $COLUMN_IS_COMPLETED = 0", arrayOf(currDate))

            // After 7 days, if not canceled/completed, uncompleted manual tasks are saved as terminated (purged from overdue)
            val currFixed = FixedCalendarHelper.parseDateStr(currDate)
            if (currFixed != null) {
                val currMs = FixedCalendarHelper.toTimestamp(currFixed)
                val sevenDaysAgoMs = currMs - 7 * 86400000L
                val sevenDaysAgoStr = FixedCalendarHelper.fromTimestamp(sevenDaysAgoMs).toString()
                db.delete(TABLE_TASKS, "$COLUMN_ASSOCIATED_DATE < ? AND $COLUMN_IS_COMPLETED = 0", arrayOf(sevenDaysAgoStr))
            }

            // Purge any existing duplicate generated tasks for the same associated_date and recurring_parent_id
            db.execSQL("""
                DELETE FROM $TABLE_TASKS 
                WHERE $COLUMN_IS_GENERATED = 1 AND $COLUMN_RECURRING_PARENT_ID IS NOT NULL AND rowid NOT IN (
                    SELECT MIN(rowid) 
                    FROM $TABLE_TASKS 
                    WHERE $COLUMN_IS_GENERATED = 1 AND $COLUMN_RECURRING_PARENT_ID IS NOT NULL
                    GROUP BY $COLUMN_ASSOCIATED_DATE, $COLUMN_RECURRING_PARENT_ID
                )
            """.trimIndent())
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        notifyWidgetUpdate()
        return 0
    }



    actual fun insertGeneratedTask(task: AppTask): Boolean {
        return insertTask(task)
    }

    actual fun hasGeneratedInstanceForDate(dateStr: String, parentId: String): Boolean {
        val db = helper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_TASKS WHERE $COLUMN_ASSOCIATED_DATE = ? AND $COLUMN_RECURRING_PARENT_ID = ?",
            arrayOf(dateStr, parentId)
        )
        return cursor.use { c ->
            c.moveToFirst() && c.getInt(0) > 0
        }
    }

    actual fun deleteIncompleteGeneratedTasks(parentId: String): Int {
        val db = helper.writableDatabase
        return db.delete(TABLE_TASKS, "$COLUMN_RECURRING_PARENT_ID = ? AND $COLUMN_IS_COMPLETED = 0", arrayOf(parentId))
    }

    private fun cursorToTask(c: android.database.Cursor): AppTask {
        return AppTask(
            id = c.getString(c.getColumnIndexOrThrow(COLUMN_ID)),
            title = c.getString(c.getColumnIndexOrThrow(COLUMN_TITLE)),
            description = c.getString(c.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
            associatedDate = c.getString(c.getColumnIndexOrThrow(COLUMN_ASSOCIATED_DATE)),
            isReminder = c.getInt(c.getColumnIndexOrThrow(COLUMN_IS_REMINDER)),
            reminderTime = c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_TIME)),
            utcTimestamp = if (c.isNull(c.getColumnIndexOrThrow(COLUMN_UTC_TIMESTAMP))) null else c.getLong(c.getColumnIndexOrThrow(COLUMN_UTC_TIMESTAMP)),
            isCompleted = c.getInt(c.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)),
            priority = c.getInt(c.getColumnIndexOrThrow(COLUMN_PRIORITY)),
            recurringParentId = c.getString(c.getColumnIndexOrThrow(COLUMN_RECURRING_PARENT_ID)),
            isGenerated = c.getInt(c.getColumnIndexOrThrow(COLUMN_IS_GENERATED))
        )
    }

    private fun cursorToRecurringTask(c: android.database.Cursor): RecurringTask {
        val daysStr = c.getString(c.getColumnIndexOrThrow(COLUMN_REC_DAYS))
        val daysList = if (daysStr.isNullOrEmpty()) {
            emptyList()
        } else {
            daysStr.split(",").mapNotNull { it.toIntOrNull() }
        }

        val typeStr = c.getString(c.getColumnIndexOrThrow(COLUMN_REC_TYPE))
        val recurrenceType = RecurrenceType.entries.find { it.name == typeStr } ?: RecurrenceType.DAILY

        return RecurringTask(
            id = c.getString(c.getColumnIndexOrThrow(COLUMN_REC_ID)),
            title = c.getString(c.getColumnIndexOrThrow(COLUMN_REC_TITLE)),
            description = c.getString(c.getColumnIndexOrThrow(COLUMN_REC_DESCRIPTION)),
            recurrenceType = recurrenceType,
            recurrenceDays = daysList,
            recurrenceInterval = c.getInt(c.getColumnIndexOrThrow(COLUMN_REC_INTERVAL)),
            priority = c.getInt(c.getColumnIndexOrThrow(COLUMN_REC_PRIORITY)),
            isActive = c.getInt(c.getColumnIndexOrThrow(COLUMN_REC_IS_ACTIVE)) == 1,
            createdAt = c.getLong(c.getColumnIndexOrThrow(COLUMN_REC_CREATED_AT)),
            endDate = c.getString(c.getColumnIndexOrThrow(COLUMN_REC_END_DATE)),
            reminderTime = c.getString(c.getColumnIndexOrThrow(COLUMN_REC_REMINDER_TIME))
        )
    }

    companion object {
        const val DATABASE_NAME = "fixed_calendar.db"
        const val DATABASE_VERSION = 2

        const val TABLE_TASKS = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_ASSOCIATED_DATE = "associated_date"
        const val COLUMN_IS_REMINDER = "is_reminder"
        const val COLUMN_REMINDER_TIME = "reminder_time"
        const val COLUMN_UTC_TIMESTAMP = "utc_timestamp"
        const val COLUMN_IS_COMPLETED = "is_completed"
        const val COLUMN_PRIORITY = "priority"
        const val COLUMN_RECURRING_PARENT_ID = "recurring_parent_id"
        const val COLUMN_IS_GENERATED = "is_generated"

        const val TABLE_RECURRING = "recurring_tasks"
        const val COLUMN_REC_ID = "id"
        const val COLUMN_REC_TITLE = "title"
        const val COLUMN_REC_DESCRIPTION = "description"
        const val COLUMN_REC_TYPE = "recurrence_type"
        const val COLUMN_REC_DAYS = "recurrence_days"
        const val COLUMN_REC_INTERVAL = "recurrence_interval"
        const val COLUMN_REC_PRIORITY = "priority"
        const val COLUMN_REC_IS_ACTIVE = "is_active"
        const val COLUMN_REC_CREATED_AT = "created_at"
        const val COLUMN_REC_END_DATE = "end_date"
        const val COLUMN_REC_REMINDER_TIME = "reminder_time"
    }
}
