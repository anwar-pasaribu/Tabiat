package features.workoutHistory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    targetDateTimeStamp: Long
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
        showFullScreen = false
    ) {

        Text(
            text = "Riwayat Latihan",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.Start).padding(start = 8.dp)
        )

        Text(
            text = targetDateTimeStamp.epochTimestampToLongDateFormat(),
            modifier = Modifier.align(Alignment.Start).padding(start = 8.dp)
        )

        if (itemList.isNotEmpty()) {
            ExerciseHistoryDailyPieChart(itemList)
        }

        LazyColumn(
            modifier = modifier.then(Modifier.fillMaxWidth().defaultMinSize(minHeight = 300.dp)),
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 32.dp)
        ) {

            itemsIndexed(items = itemList, key = { _, item -> item.exerciseLogId }) { index, item ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {

                        Row(modifier = Modifier.align(Alignment.CenterStart)) {
                            Text(
                                text = (index + 1).toString().padEnd(4, ' '),
                                style = MaterialTheme.typography.labelSmall,
                            )
                            Spacer(Modifier.width(4.dp))
                            Column() {
                                Text(
                                    text = item.exerciseName,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                                Text(
                                    text = item.exerciseTargetMuscle.firstOrNull().orEmpty(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                )
                            }
                        }

                        ExerciseSetBadge(modifier = Modifier.align(Alignment.Center)) {
                            Text(
                                text = "${item.reps} ✕ ${item.weight.toInt()}",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }

                        Text(
                            text = item.finishedDateTime.epochTimestampToShortDateTimeFormat(),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.align(Alignment.CenterEnd)
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

private val rainbowColors = listOf(
    Color(0xFFB71C1C), // Deep Red
    Color(0xFFF57C00), // Deep Orange
    Color(0xFFFBC02D), // Mustard Yellow
    Color(0xFF388E3C), // Forest Green
    Color(0xFF1976D2), // Cobalt Blue
    Color(0xFF303F9F), // Indigo
    Color(0xFF8E24AA)  // Deep Violet
)

@Composable
fun ExerciseHistoryDailyPieChart(itemList: List<ExerciseHistoryUiItem>) {

    val combinedTargetMuscle = itemList.flatMap { it.exerciseTargetMuscle.take(1) }
    val muscleGroupCount = combinedTargetMuscle.groupingBy { it }.eachCount()
    val pieChartListData = mutableListOf<PieChartData>()

    // Sort the muscle groups by count in descending order and take the top 6
    val sortedMuscleGroups = muscleGroupCount.entries.sortedByDescending { it.value }
    val topMuscleGroups = sortedMuscleGroups.take(6)
    val otherMuscleGroups = sortedMuscleGroups.drop(6)

    val otherMuscleGroupCount = otherMuscleGroups.sumOf { it.value }

    val dataSize = topMuscleGroups.size + if (otherMuscleGroupCount > 0) 1 else 0
    val colors = remember(dataSize) {
        derivedStateOf {
            (0 until dataSize).map {
                rainbowColors[it % rainbowColors.size]
            }
        }
    }

    // Add the top muscle groups to the pie chart data
    topMuscleGroups.forEachIndexed { index, entry ->
        pieChartListData.add(
            PieChartData(
                partName = entry.key,
                data = entry.value.toDouble(),
                color = colors.value[index]
            )
        )
    }

    // Add the 'Lainnya' group if there are any other muscle groups
    if (otherMuscleGroupCount > 0) {
        pieChartListData.add(
            PieChartData(
                partName = "Lainnya",
                data = otherMuscleGroupCount.toDouble(),
                color = colors.value.last()
            )
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
            legendPosition = LegendPosition.BOTTOM
        )
    }

}

private fun Long.epochTimestampToShortDateTimeFormat(): String {
    // Convert epoch timestamp to LocalDateTime object
    val dateTime = Instant.fromEpochMilliseconds(
        this
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
        this
    ).toLocalDateTime(TimeZone.currentSystemDefault())

    // Format the date using the desired pattern
    val dayOfMonth = dateTime.dayOfMonth.toString().padStart(2, '0')
    val month = dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }
    val year = dateTime.year
    return "$dayOfMonth $month $year"
}