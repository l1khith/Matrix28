package com.l1khith.matrix28.utils

import androidx.compose.runtime.Composable

expect fun copyToClipboard(text: String)

@Composable
expect fun PlatformTimePicker(
    show: Boolean,
    initialTime: String,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
)
