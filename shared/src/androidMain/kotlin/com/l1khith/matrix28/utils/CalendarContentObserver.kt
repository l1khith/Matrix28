package com.l1khith.matrix28.utils

import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract
import android.util.Log
import com.l1khith.matrix28.data.AppContext
import com.l1khith.matrix28.data.TaskDatabase
import kotlin.concurrent.thread

object CalendarContentObserver {
    private var observer: ContentObserver? = null

    fun register() {
        if (observer != null) return
        try {
            val context = AppContext.get()
            val handler = Handler(Looper.getMainLooper())
            observer = object : ContentObserver(handler) {
                override fun onChange(selfChange: Boolean, uri: Uri?) {
                    super.onChange(selfChange, uri)
                    Log.d("CalendarObserver", "System calendar update detected: $uri")
                    thread {
                        try {
                            val systemEvents = importSystemCalendarEvents()
                            if (systemEvents.isNotEmpty()) {
                                val db = TaskDatabase()
                                for (task in systemEvents) {
                                    db.insertTask(task)
                                }
                                com.l1khith.matrix28.widget.WidgetUpdater.updateWidget()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            context.contentResolver.registerContentObserver(
                CalendarContract.Events.CONTENT_URI,
                true,
                observer!!
            )
            Log.d("CalendarObserver", "Registered automatic CalendarContentObserver")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unregister() {
        try {
            if (observer != null) {
                val context = AppContext.get()
                context.contentResolver.unregisterContentObserver(observer!!)
                observer = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
