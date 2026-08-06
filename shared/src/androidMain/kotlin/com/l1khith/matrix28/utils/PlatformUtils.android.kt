package com.l1khith.matrix28.utils

import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.l1khith.matrix28.data.AppContext

actual fun copyToClipboard(text: String) {
    try {
        val context = AppContext.get()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Calendar Event ICS", text)
        clipboard.setPrimaryClip(clip)
    } catch (_: Exception) {}
}

@Composable
actual fun PlatformTimePicker(
    show: Boolean,
    initialTime: String,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    if (!show) return
    val context = LocalContext.current
    var hour = 7
    var min = 0
    try {
        val clean = initialTime.replace(" Daily", "")
        val parts = clean.split(" ", ":")
        hour = parts.getOrNull(0)?.toIntOrNull() ?: 7
        min = parts.getOrNull(1)?.toIntOrNull() ?: 0
        if (clean.contains("PM", ignoreCase = true) && hour < 12) hour += 12
        if (clean.contains("AM", ignoreCase = true) && hour == 12) hour = 0
    } catch (_: Exception) {}

    DisposableEffect(show) {
        val dialog = TimePickerDialog(
            context,
            { _, h, m ->
                val amPm = if (h >= 12) "PM" else "AM"
                val hour12 = when {
                    h == 0 -> 12
                    h > 12 -> h - 12
                    else -> h
                }
                val formatted = "${hour12.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')} $amPm"
                onTimeSelected(formatted)
            },
            hour,
            min,
            false
        )
        dialog.setOnDismissListener { onDismiss() }
        dialog.show()
        onDispose {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }
}
