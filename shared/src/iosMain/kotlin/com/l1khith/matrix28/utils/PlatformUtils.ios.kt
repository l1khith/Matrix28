package com.l1khith.matrix28.utils

import androidx.compose.runtime.Composable
import platform.UIKit.UIPasteboard

actual fun copyToClipboard(text: String) {
    UIPasteboard.generalPasteboard.string = text
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
            show = true,
            initialTime = initialTime,
            onDismiss = onDismiss,
            onTimeSelected = onTimeSelected
        )
    }
}
