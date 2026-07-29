package com.l1khith.matrix28

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MAtrix28",
    ) {
        App()
    }
}