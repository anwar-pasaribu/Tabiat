package com.unwur.tabiatmu.playground

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.component.gym.AddExerciseSet
import ui.component.gym.AddWorkoutLog
import ui.theme.MyAppTheme


@Preview
@Composable
private fun AddWorkoutLogPrev() {
    MyAppTheme {
        Column {
            AddWorkoutLog(Modifier)
            Spacer(modifier = Modifier.height(16.dp))
            AddExerciseSet()
        }
    }
}