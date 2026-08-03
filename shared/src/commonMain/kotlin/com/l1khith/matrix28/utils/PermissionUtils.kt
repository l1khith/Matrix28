package com.l1khith.matrix28.utils

import androidx.compose.runtime.Composable

expect fun showToast(message: String)

@Composable
expect fun rememberCalendarPermissionLauncher(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): () -> Unit

@Composable
expect fun rememberNotificationPermissionLauncher(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): () -> Unit
