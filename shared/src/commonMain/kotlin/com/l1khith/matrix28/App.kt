package com.l1khith.matrix28

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.l1khith.matrix28.ui.FixedCalendarApp
import com.l1khith.matrix28.viewmodel.FixedCalendarViewModel

@Composable
fun App() {
    val isDark = isSystemInDarkTheme()
    val colors = if (isDark) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = colors) {
        val viewModel: FixedCalendarViewModel = viewModel { FixedCalendarViewModel() }
        FixedCalendarApp(viewModel = viewModel)
    }
}