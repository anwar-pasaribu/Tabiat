package com.unwur.tabiatmu.playground

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.component.gym.AddWorkoutLog
import ui.theme.MyAppTheme


@Preview
@Composable
private fun AddWorkoutLogPrev() {
    MyAppTheme {
        AddWorkoutLog(Modifier)
    }
}