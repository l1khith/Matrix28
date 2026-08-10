package com.l1khith.matrix28.widget

import java.util.concurrent.CopyOnWriteArrayList

actual object WidgetUpdater {
    private val callbacks = CopyOnWriteArrayList<() -> Unit>()

    fun registerUpdateCallback(callback: () -> Unit) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback)
        }
    }

    fun unregisterUpdateCallback(callback: () -> Unit) {
        callbacks.remove(callback)
    }

    actual fun updateWidget() {
        try {
            callbacks.forEach { it.invoke() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
