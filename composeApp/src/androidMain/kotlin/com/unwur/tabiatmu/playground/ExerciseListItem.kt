package com.unwur.tabiatmu.playground

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.component.gym.ExerciseListItemView
import ui.theme.MyAppTheme


@Preview
@Composable
private fun WorkoutListItemViewPreview() {
    MyAppTheme {
        ExerciseListItemView(
            Modifier,
            title = "Exercise title",
            description = "Exercise description"
        )
    }
}