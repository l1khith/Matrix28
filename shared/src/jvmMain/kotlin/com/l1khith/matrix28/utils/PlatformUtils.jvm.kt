package com.l1khith.matrix28.utils

import androidx.compose.runtime.Composable
import com.l1khith.matrix28.ui.TimePickerDialog
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

actual fun copyToClipboard(text: String) {
    try {
        val selection = StringSelection(text)
        Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
    } catch (_: Exception) {}
}

@Composable
actual fun PlatformTimePicker(
    show: Boolean,
    initialTime: String,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    if (show) {
        TimePickerDialog(
            initialTime = initialTime,
            onDismiss = onDismiss,
            onTimeSelected = onTimeSelected
        )
    }
}
