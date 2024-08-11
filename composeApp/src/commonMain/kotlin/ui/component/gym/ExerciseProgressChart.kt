/*
 * MIT License
 *
 * Copyright (c) 2024 Anwar Pasaribu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Project Name: Tabiat
 */
package ui.component.gym

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType

@Stable
data class ExerciseAxisData(
    val title: String,
    val xAxis: List<Double>,
    val yAxis: List<Long>,
)

@Composable
fun ExerciseProgressLineChart(
    modifier: Modifier = Modifier,
    axisData: ExerciseAxisData,
) {
    if (axisData.xAxis.isEmpty() || axisData.yAxis.isEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(156.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Belum pernah latihan ini ya?")
            }
        }
        return
    }

    Card(modifier = modifier.then(Modifier.height(156.dp))) {
        Column(modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp)) {
            val lineColor = MaterialTheme.colorScheme.primary.copy(alpha = .85f)
            val gridColor = MaterialTheme.colorScheme.primary.copy(alpha = .5f)

            val listData = axisData.xAxis

            val testLineParameters: List<LineParameters> = listOf(
                LineParameters(
                    label = "Progres Latihan • ${axisData.title}",
                    data = listData,
                    lineColor = lineColor,
                    lineType = LineType.CURVED_LINE,
                    lineShadow = true,
                ),
            )

            com.aay.compose.lineChart.LineChart(
                modifier = Modifier.fillMaxSize(),
                linesParameters = testLineParameters,
                isGrid = true,
                gridColor = gridColor,
                xAxisData = listData.map { it.toString() },
                animateChart = true,
                showGridWithSpacer = false,
                showXAxis = false,
                yAxisRange = listData.size,
                oneLineChart = false,
                gridOrientation = GridOrientation.HORIZONTAL,
            )
        }
    }
}
