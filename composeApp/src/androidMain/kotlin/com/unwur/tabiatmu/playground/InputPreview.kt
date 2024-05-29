package com.unwur.tabiatmu.playground

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.component.gym.ExerciseSetBadge

@Preview(showBackground = true)
@Composable
private fun InputPrev() {
    MaterialTheme {
        ExerciseSetBadge(Modifier) {
            Text(text = "10 x 10kg", style = MaterialTheme.typography.headlineLarge)
        }
    }
}