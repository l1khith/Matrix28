package com.l1khith.matrix28.utils

import androidx.compose.runtime.Composable
import platform.UIKit.UIPasteboard

actual fun copyToClipboard(text: String) {
    UIPasteboard.generalPasteboard.string = text
}

actual fun showPlatformToast(message: String) {
    // iOS no-op
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

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS no-op
}
