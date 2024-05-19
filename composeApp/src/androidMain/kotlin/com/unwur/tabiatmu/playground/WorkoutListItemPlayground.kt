package com.unwur.tabiatmu.playground

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.component.gym.WorkoutListItemView
import ui.theme.MyAppTheme


@Preview(showBackground = true)
@Composable
private fun WorkoutListItemViewPreview() {
    MyAppTheme {
        Column {

            WorkoutListItemView(
                modifier = Modifier,
                title = "Workout",
                description = "Workout description",
                progress = 7,
                total = 10
            )
            Spacer(modifier = Modifier.height(16.dp))
            WorkoutListItemView(
                modifier = Modifier,
                title = "Workout 2",
                description = "Workout description panjang",
                progress = 10,
                total = 10
            )
            Spacer(modifier = Modifier.height(16.dp))
            WorkoutListItemView(
                modifier = Modifier,
                title = "Workout without desc",
                description = ""
            )
            WorkPlanProgressView(total = 5, progress = 3)
        }
    }
}

@Composable
fun WorkPlanProgressView(
    modifier: Modifier = Modifier,
    total: Int,
    progress: Int
) {
    Row {
        Text(text = "$progress dari $total selesai")
        CircularProgressIndicator(
            progress = {
                       progress/total.toFloat()
            },
            modifier = Modifier.size(size = 40.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp,
            strokeCap = StrokeCap.Round,
        )
    }
}