package com.l1khith.matrix28.utils

import androidx.compose.runtime.Composable

actual fun showToast(message: String) {}

@Composable
actual fun rememberCalendarPermissionLauncher(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): () -> Unit {
    return { onGranted() }
}

@Composable
actual fun rememberNotificationPermissionLauncher(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): () -> Unit {
    return { onGranted() }
}
