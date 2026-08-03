package com.l1khith.matrix28.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.l1khith.matrix28.data.AppContext
import com.l1khith.matrix28.data.TaskDatabase
import com.l1khith.matrix28.utils.FixedCalendarHelper
import com.l1khith.matrix28.utils.currentTimeMillis
import com.l1khith.matrix28.utils.scheduleTaskAlarm
import kotlin.concurrent.thread

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        thread {
            try {
                AppContext.init(context)
                val action = intent.action ?: return@thread

                // Validate package scope if intent is custom action (Bug #24)
                val isMidnightRollover = action == "com.l1khith.matrix28.ACTION_MIDNIGHT_ROLLOVER"
                if (isMidnightRollover && intent.`package` != context.packageName) {
                    return@thread
                }

                val isBootOrTimeChange = action == Intent.ACTION_BOOT_COMPLETED ||
                    action == Intent.ACTION_TIME_CHANGED ||
                    action == Intent.ACTION_TIMEZONE_CHANGED

                if (isBootOrTimeChange || isMidnightRollover) {
                    val db = TaskDatabase()
                    val currentDateStr = FixedCalendarHelper.fromTimestamp(currentTimeMillis()).toString()
                    db.catchUpRollover(currentDateStr)

                    if (isMidnightRollover || action == Intent.ACTION_BOOT_COMPLETED) {
                        com.l1khith.matrix28.utils.scheduleMidnightRollover()
                    }

                    if (isBootOrTimeChange) {
                        val tasks = db.getAllTasks()
                        for (task in tasks) {
                            if (task.reminder && !task.completed && task.utcTimestamp != null && task.utcTimestamp > currentTimeMillis()) {
                                scheduleTaskAlarm(task)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
