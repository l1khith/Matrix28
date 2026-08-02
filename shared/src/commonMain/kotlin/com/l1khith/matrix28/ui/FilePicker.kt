package com.l1khith.matrix28.ui

import androidx.compose.runtime.Composable

@Composable
expect fun rememberFilePickerLauncher(onFilePicked: (String) -> Unit): () -> Unit
