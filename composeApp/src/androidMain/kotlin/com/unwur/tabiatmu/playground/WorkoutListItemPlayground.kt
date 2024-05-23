package com.unwur.tabiatmu.playground

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.component.gym.LatestExercise
import ui.component.gym.WorkoutPlanItemView
import ui.theme.MyAppTheme


@Preview(showBackground = true)
@Composable
private fun WorkoutListItemViewPreview() {
    MyAppTheme {
        Column {

            WorkoutPlanItemView(
                modifier = Modifier,
                title = "Workout",
                description = "Workout description",
                progress = 7,
                total = 10
            )
            Spacer(modifier = Modifier.height(16.dp))
            WorkoutPlanItemView(
                modifier = Modifier,
                title = "Workout 2",
                description = "Workout description panjang",
                progress = 10,
                total = 10
            )
            Spacer(modifier = Modifier.height(16.dp))
            WorkoutPlanItemView(
                modifier = Modifier,
                title = "Workout without desc",
                description = "",
            )
            Spacer(modifier = Modifier.height(16.dp))
            WorkoutPlanItemView(
                modifier = Modifier,
                title = "Workout with last activity",
                description = "Description",
                progress = 9,
                total = 10,
                lastActivityInfo = {
                    LatestExercise(
                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                        exerciseImageUrl = "",
                        upperLabel = "Latihan terakhir 12 Juni 2024",
                        lowerLabel = "Barbell Bench Press (10 x 12kg)"
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun LatestExercisePrev() {
    MyAppTheme {
        LatestExercise(
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
            exerciseImageUrl = "",
            upperLabel = "Push ups",
            lowerLabel = "10"
        )
    }
    
}