package features.workoutHistory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import features.exerciseList.BottomSheet
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import ui.component.InsetNavigationHeight

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

        LazyColumn(
            modifier = modifier.then(Modifier.fillMaxWidth().defaultMinSize(minHeight = 300.dp)),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp)
        ) {

            items(items = itemList, key = { it.id }) { item ->
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
                                text = item.exerciseId.toString(),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${item.reps} ✕ ${item.weight}",
                            )
                        }

                        Text(
                            text = item.finishedDateTime.epochTimestampToShortDateTimeFormat(),
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