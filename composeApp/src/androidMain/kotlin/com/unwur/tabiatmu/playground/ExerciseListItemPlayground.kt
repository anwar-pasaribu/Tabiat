package com.unwur.tabiatmu.playground

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.component.gym.ExerciseListItemView
import ui.theme.MyAppTheme


@Preview
@Composable
private fun WorkoutListItemViewPreview() {
    MyAppTheme {
        Column {
            ExerciseListItemView(
                Modifier,
                title = "Exercise title",
                description = "Exercise description"
            )
            Spacer(modifier = Modifier.height(16.dp))
            ExerciseListItemView(
                Modifier,
                image = "URL",
                title = "Barbell exercises (e.g., squats, deadlifts, bench press)",
                description = "Barbell exercises (e.g., squats, deadlifts, bench press)"
            )
        }
    }
}