package com.l1khith.matrix28.widget

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

actual object WidgetUpdater {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    actual fun updateWidget() {
        scope.launch {
            try {
                val context = com.l1khith.matrix28.data.AppContext.get()
                TodayTaskWidget().updateAll(context)
            } catch (e: Exception) {
                Log.e("WidgetUpdater", "Failed to update widget", e)
            }
        }
    }
}
