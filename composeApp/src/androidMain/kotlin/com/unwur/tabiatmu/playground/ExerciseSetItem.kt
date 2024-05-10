package com.unwur.tabiatmu.playground

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.component.gym.ExerciseSetItemView
import ui.theme.MyAppTheme

@Preview(showBackground = true)
@Composable
private fun ExerciseSetItemViewPrev() {
    MyAppTheme {
        ExerciseSetItemView(
            modifier = Modifier,
            1,
            12,
            32,
            onSetItemClick = {}
        )
    }
}