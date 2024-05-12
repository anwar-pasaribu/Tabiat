package com.unwur.tabiatmu.playground

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.component.gym.ExerciseSetItemView
import ui.theme.MyAppTheme

@Preview(showBackground = true)
@Composable
private fun ExerciseSetItemViewPrev() {
    MyAppTheme {
        Column {
            repeat(5) {
                ExerciseSetItemView(
                    modifier = Modifier,
                    it,
                    listOf(12, 15, 16, 6, 7).random(),
                    listOf(12, 16, 18, 20, 26).random(),
                    finished = listOf(false, true).random(),
                    onSetItemClick = {}
                )
            }
        }
    }
}