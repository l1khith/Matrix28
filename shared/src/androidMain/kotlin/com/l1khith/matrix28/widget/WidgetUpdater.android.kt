package com.l1khith.matrix28.widget

actual object WidgetUpdater {
    private var updateCallback: (() -> Unit)? = null

    fun registerUpdateCallback(callback: () -> Unit) {
        updateCallback = callback
    }

    actual fun updateWidget() {
        try {
            updateCallback?.invoke()
        } catch (_: Exception) {}
    }
}
