package com.unwur.tabiatmu.playground.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ui.theme.MyAppTheme

data class PieChartData(val name: String, val value: Int, val color: Color)

@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier,
    diameter: Dp = 200.dp,
    startAngle: Float = 0f
) {
    val totalValue = data.sumOf { it.value }
    val angles = data.map { 360f * it.value / totalValue }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.size(diameter)) {
            var currentStartAngle = startAngle
            data.forEachIndexed { index, pieChartData ->
                drawArc(
                    color = pieChartData.color,
                    startAngle = currentStartAngle,
                    sweepAngle = angles[index],
                    useCenter = true,
                    size = size
                )
                currentStartAngle += angles[index]
            }
        }
    }
}

@Composable
fun PieChartWithLegend(
    data: List<PieChartData>,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        PieChart(
            data = data,
            modifier = Modifier.weight(1f)
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 16.dp)
        ) {
            data.forEach { pieChartData ->
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom= 8.dp)
                ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(pieChartData.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${pieChartData.name} (${pieChartData.value}x)")
            }
            }
        }
    }
}

@Composable
fun SamplePieChart() {
    val data = listOf(
        PieChartData("chest", 5, Color.Red),
        PieChartData("arm", 6, Color.Blue),
        PieChartData("biceps", 3, Color.Green)
    )

    PieChartWithLegend(
        data = data,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    )
}

@Preview
@Composable
private fun PieChartPrev() {
    MyAppTheme {
        SamplePieChart()
    }
}
