package com.unwur.tabiatmu.playground

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.component.gym.ExerciseSetItemView
import ui.theme.MyAppTheme

@Preview(showBackground = true)
@Composable
private fun ExerciseSetItemViewPrev() {
    MyAppTheme {
        Card {

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
                Spacer(modifier = Modifier.height(16.dp))
                ExerciseSetItemView(
                    modifier = Modifier,
                    9,
                    listOf(12, 15, 16, 6, 7).random(),
                    listOf(12, 16, 18, 20, 26).random(),
                    finished = listOf(false, true).random(),
                    onSetItemClick = {},
                    stateIcon = {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "")
                    }
                )
            }
        }
    }
}