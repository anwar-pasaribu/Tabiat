package com.unwur.tabiatmu.playground

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ui.component.gym.ExerciseFinishingStatusView
import ui.theme.MyAppTheme


@Preview
@Composable
private fun ExerciseFinishingStatusViewPrev() {
    MyAppTheme {
        ExerciseFinishingStatusView(
            total = 4,
            progress = 3
        )
    }
}