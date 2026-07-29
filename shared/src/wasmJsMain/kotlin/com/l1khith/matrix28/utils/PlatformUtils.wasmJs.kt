package com.l1khith.matrix28.utils

import androidx.compose.runtime.Composable
import com.l1khith.matrix28.ui.TimePickerDialog

actual fun copyToClipboard(text: String) {}

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
