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
package features.workoutHistory

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aay.compose.baseComponents.model.LegendPosition
import com.aay.compose.donutChart.PieChart
import com.aay.compose.donutChart.model.PieChartData
import features.exerciseList.BottomSheet
import features.workoutHistory.model.ExerciseHistoryUiItem
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import ui.component.InsetNavigationHeight
import ui.component.gym.ExerciseSetBadge

@Composable
fun ExerciseLogListBottomSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    targetDateTimeStamp: Long,
) {
    val viewModel = koinInject<WorkoutHistoryScreenViewModel>()
    val itemList by viewModel.exerciseLogList.collectAsState()

    LaunchedEffect(targetDateTimeStamp) {
        viewModel.getExerciseLogList(targetDateTimeStamp)
    }

    BottomSheet(
        onDismiss = {
            onDismiss()
        },
        showFullScreen = false,
    ) {
        Text(
            text = "Riwayat Latihan",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.Start).padding(start = 8.dp),
        )

        LogHistoryDetailView(
            itemList.size,
            targetDateTimeStamp,
        )

        val darkMode = isSystemInDarkTheme()
        DisposableEffect(darkMode) {
            onDispose { }
        }
        if (itemList.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            ExerciseHistoryDailyPieChart(itemList, isSystemInDarkTheme())
        }

        LazyColumn(
            modifier = modifier.then(Modifier.fillMaxWidth().defaultMinSize(minHeight = 300.dp)),
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 32.dp),
        ) {
            itemsIndexed(items = itemList, key = { _, item -> item.exerciseLogId }) { index, item ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                    shape = MaterialTheme.shapes.extraSmall,
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Row(
                            modifier = Modifier.align(Alignment.CenterStart),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = (index + 1).toString().padEnd(4, ' '),
                                style = MaterialTheme.typography.labelMedium,
                            )
                            Spacer(Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = item.exerciseName,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                                Text(
                                    text = item.exerciseTargetMuscle.firstOrNull().orEmpty(),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                    ),
                                )
                            }
                        }

                        ExerciseSetBadge(modifier = Modifier.align(Alignment.Center)) {
                            Text(
                                text = "${item.reps} ✕ ${item.weight.toInt()}",
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }

                        Text(
                            text = item.finishedDateTime.epochTimestampToShortDateTimeFormat(),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.align(Alignment.CenterEnd),
                        )
                    }
                }
            }

            item {
                InsetNavigationHeight()
            }
        }
    }
}

@Composable
private fun ColumnScope.LogHistoryDetailView(size: Int, targetDateTimeStamp: Long) {
    Row(
        modifier = Modifier.align(Alignment.Start).padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (size != 0) {
            Text(
                text = "$size Latihan",
            )
        } else {
            Text(
                text = "Tidak ada latihan",
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Box(
            Modifier.size(4.dp).background(MaterialTheme.colorScheme.onBackground, CircleShape),
        )
        Text(
            text = targetDateTimeStamp.epochTimestampToLongDateFormat(),
        )
    }
}

private val darkRainbowColors = listOf(
    Color(0xFFB22222),
    Color(0xFFD2691E),
    Color(0xFFB8860B),
    Color(0xFF006400),
    Color(0xFF008B8B),
    Color(0xFF00008B),
    Color(0xFF8B008B),
)

private val lightRainbowColors = listOf(
    Color(0xFFB71C1C),
    Color(0xFFF57C00),
    Color(0xFFFBC02D),
    Color(0xFF388E3C),
    Color(0xFF1976D2),
    Color(0xFF303F9F),
    Color(0xFF8E24AA),
)

@Composable
fun ExerciseHistoryDailyPieChart(itemList: List<ExerciseHistoryUiItem>, darkMode: Boolean) {
    val combinedTargetMuscle = itemList.flatMap { it.exerciseTargetMuscle }
    val muscleGroupCount = combinedTargetMuscle.groupingBy { it }.eachCount()
    val pieChartListData = mutableListOf<PieChartData>()

    // Sort the muscle groups by count in descending order and take the top 6
    val sortedMuscleGroups = muscleGroupCount.entries.sortedByDescending { it.value }
    val topMuscleGroups = sortedMuscleGroups.take(6)
    val otherMuscleGroups = sortedMuscleGroups.drop(6)
    val otherMuscleGroupCount = otherMuscleGroups.sumOf { it.value }

    val dataSize = topMuscleGroups.size + if (otherMuscleGroupCount > 0) 1 else 0

    val color = if (darkMode) {
        darkRainbowColors
    } else {
        lightRainbowColors
    }
    val colors = remember(dataSize) {
        derivedStateOf {
            (0 until dataSize).map {
                color[it % color.size]
            }
        }
    }

    // Add the top muscle groups to the pie chart data
    topMuscleGroups.forEachIndexed { index, entry ->
        pieChartListData.add(
            PieChartData(
                partName = entry.key,
                data = entry.value.toDouble(),
                color = colors.value[index],
            ),
        )
    }

    // Add the 'Lainnya' group if there are any other muscle groups
    if (otherMuscleGroupCount > 0) {
        pieChartListData.add(
            PieChartData(
                partName = "Lainnya",
                data = otherMuscleGroupCount.toDouble(),
                color = colors.value.last(),
            ),
        )
    }

    Surface {
        PieChart(
            modifier = Modifier.fillMaxWidth().height(280.dp),
            pieChartData = pieChartListData,
            ratioLineColor = MaterialTheme.colorScheme.primary,
            outerCircularColor = MaterialTheme.colorScheme.primary,
            textRatioStyle = MaterialTheme.typography.labelMedium,
            descriptionStyle = MaterialTheme.typography.labelLarge,
            legendPosition = LegendPosition.BOTTOM,
        )
    }
}

private fun Long.epochTimestampToShortDateTimeFormat(): String {
    // Convert epoch timestamp to LocalDateTime object
    val dateTime = Instant.fromEpochMilliseconds(
        this,
    ).toLocalDateTime(TimeZone.currentSystemDefault())

    // Format the date using the desired pattern
    val dayOfMonth = dateTime.dayOfMonth.toString().padStart(2, '0')
    val month = dateTime.month.number.toString().padStart(2, '0')
    val year = dateTime.year
    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')
    return "$dayOfMonth/$month/$year, $hour:$minute"
}

private fun Long.epochTimestampToLongDateFormat(): String {
    // Convert epoch timestamp to LocalDateTime object
    val dateTime = Instant.fromEpochMilliseconds(
        this,
    ).toLocalDateTime(TimeZone.currentSystemDefault())

    // Format the date using the desired pattern
    val dayOfMonth = dateTime.dayOfMonth.toString().padStart(2, '0')
    val month = dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }
    val year = dateTime.year
    return "$dayOfMonth $month $year"
}
