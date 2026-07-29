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
    val parts = initialTime.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 12
    val min = parts.getOrNull(1)?.toIntOrNull() ?: 0

    DisposableEffect(Unit) {
        val dialog = TimePickerDialog(
            context,
            { _, h, m ->
                val formatted = "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}"
                onTimeSelected(formatted)
            },
            hour,
            min,
            true
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
