package com.l1khith.matrix28.ui

import androidx.compose.runtime.Composable

@Composable
actual fun rememberFilePickerLauncher(onFilePicked: (String) -> Unit): () -> Unit {
    return { }
}
