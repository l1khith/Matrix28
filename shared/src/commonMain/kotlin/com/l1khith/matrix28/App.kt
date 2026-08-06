package com.l1khith.matrix28

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.l1khith.matrix28.ui.FixedCalendarApp
import com.l1khith.matrix28.ui.theme.MatrixColorScheme
import com.l1khith.matrix28.viewmodel.FixedCalendarViewModel

@Composable
fun App() {
    MaterialTheme(colorScheme = MatrixColorScheme) {
        val viewModel: FixedCalendarViewModel = viewModel { FixedCalendarViewModel() }
        FixedCalendarApp(viewModel = viewModel)
    }
}