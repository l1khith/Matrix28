package com.l1khith.matrix28.utils

import androidx.compose.runtime.Composable

expect fun copyToClipboard(text: String)
expect fun showPlatformToast(message: String)

@Composable
expect fun PlatformTimePicker(
    show: Boolean,
    initialTime: String,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
)

@Composable
expect fun PlatformBackHandler(enabled: Boolean = true, onBack: () -> Unit)

// ===== APP LOCK: expect declaration =====
@Composable
expect fun rememberSecurityLockLauncher(): () -> Unit
