package com.l1khith.matrix28

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.l1khith.matrix28.ui.FixedCalendarApp
import com.l1khith.matrix28.ui.theme.MatrixTheme
import com.l1khith.matrix28.viewmodel.FixedCalendarViewModel

@Composable
fun App(initialTaskId: String? = null) {
    MatrixTheme {
        val viewModel: FixedCalendarViewModel = viewModel { FixedCalendarViewModel() }
        FixedCalendarApp(viewModel = viewModel, initialTaskId = initialTaskId)
    }
}