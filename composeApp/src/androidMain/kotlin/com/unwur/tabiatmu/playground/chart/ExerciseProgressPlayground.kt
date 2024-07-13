package com.unwur.tabiatmu.playground.chart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.component.gym.ExerciseAxisData
import ui.component.gym.ExerciseProgressLineChart
import ui.theme.MyAppTheme


@Preview
@Composable
private fun ExerciseProgressPlaygroundPrev() {
    MyAppTheme {

        Surface() {
            Column(modifier = Modifier.padding(start = 6.dp, top = 6.dp, bottom = 10.dp)) {
                val listData = listOf(70.0, 60.0, 40.0, 70.0, 60.0, 40.0, 70.0, 60.0, 40.0, 70.0, 60.0, 40.0)
                val axisData = ExerciseAxisData(
                    title = "Latihan 1",
                    xAxis = listData,
                    yAxis = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
                )
                ExerciseProgressLineChart(axisData = axisData)
            }
        }
    }
}