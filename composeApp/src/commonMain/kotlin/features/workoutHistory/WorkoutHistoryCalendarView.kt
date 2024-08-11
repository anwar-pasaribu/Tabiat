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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import features.workoutHistory.model.DayCalendarData
import features.workoutHistory.model.MonthCalendarData
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn

@Composable
private fun EmojiCalendarCell(
    cellText: String,
    isToday: Boolean = false,
    enabled: Boolean = true,
    isFuture: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cellExtraContent:
    @Composable()
    (BoxScope.() -> Unit) = {},
) {
    val circleShape = remember { CircleShape }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxSize(),
    ) {
        Box(
            modifier = modifier
                .padding(2.dp)
                .aspectRatio(1f)
                .clip(circleShape)
                .background(
                    MaterialTheme.colorScheme.surfaceContainerHighest.copy(
                        alpha = if (isFuture || !enabled) 0.25f else 1f,
                    ),
                )
                .clickable(enabled = enabled) { onClick() }
                .fillMaxSize(),
        ) {
            if (isToday) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.errorContainer),
                )
            }
            Text(
                text = cellText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                    alpha = if (isFuture) 0.25f else 1f,
                ),
                modifier = Modifier.align(Alignment.Center),
            )
        }

        cellExtraContent()
    }
}

@Composable
private fun WeekdayCell(weekday: Int, modifier: Modifier = Modifier) {
    val text = remember {
        DayOfWeek(weekday).name.take(3).lowercase().replaceFirstChar { it.uppercaseChar() }
    }
    Box(
        modifier = modifier.fillMaxSize().padding(vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun EmojiCalendarGrid(
    monthCalendarData: MonthCalendarData,
    onClick: (DayCalendarData) -> Unit,
    modifier: Modifier = Modifier,
) {
    val date = monthCalendarData.month
    val dayOfMonth = date.dayOfMonth
    val day1ThisMonth = date.minus(dayOfMonth - 1, DateTimeUnit.DAY)
    val diffDaysLastMonth = kotlin.math.abs(day1ThisMonth.dayOfWeek.isoDayNumber - 1)
    val weekdays = remember { (1..7) }
    val dayOfYearOfToday = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()).dayOfYear }

    EmojiCalendarCustomLayout(modifier = modifier) {
        weekdays.forEach {
            WeekdayCell(weekday = it)
        }

        // Adds Spacers to align the first day of the month to the correct weekday
        repeat(diffDaysLastMonth) {
            Spacer(modifier = Modifier.fillMaxSize())
        }

        monthCalendarData.dailyDataList.forEach {
            val containData = it.exerciseActivityCount >= 1
            val isToday = it.day.dayOfYear == dayOfYearOfToday
            val dotColor = if (isToday) Color.Red else MaterialTheme.colorScheme.primary
            EmojiCalendarCell(
                cellText = it.day.dayOfMonth.toString(),
                isToday = isToday,
                enabled = containData,
                isFuture = it.isFutureDate,
                onClick = { onClick(it) },
                cellExtraContent = {
                    if (containData) {
                        Box(
                            Modifier
                                .padding(bottom = 6.dp)
                                .background(dotColor, CircleShape)
                                .size(6.dp)
                                .align(Alignment.BottomCenter),
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun EmojiCalendarCustomLayout(
    modifier: Modifier = Modifier,
    horizontalGapDp: Dp = 2.dp,
    verticalGapDp: Dp = 2.dp,
    content: @Composable () -> Unit,
) {
    val horizontalGap = with(LocalDensity.current) {
        horizontalGapDp.roundToPx()
    }
    val verticalGap = with(LocalDensity.current) {
        verticalGapDp.roundToPx()
    }
    Layout(
        content = content,
        modifier = modifier,
    ) { measurables, constraints ->
        val totalWidthWithoutGap = constraints.maxWidth - (horizontalGap * 6)
        val singleWidth = totalWidthWithoutGap / 7

        val xPos: MutableList<Int> = mutableListOf()
        val yPos: MutableList<Int> = mutableListOf()
        var currentX = 0
        var currentY = 0
        var isFirstRow = true // Flag to indicate whether it's the first row
        measurables.forEachIndexed { _, _ ->
            xPos.add(currentX)
            yPos.add(currentY)

            // Adjust height for the first row
            val cellHeight = if (isFirstRow) singleWidth / 2 else singleWidth

            if (currentX + singleWidth + horizontalGap > totalWidthWithoutGap) {
                currentX = 0
                currentY += cellHeight + verticalGap
                isFirstRow = false // After the first row, set the flag to false
            } else {
                currentX += singleWidth + horizontalGap
            }
        }

        val placeables: List<Placeable> = measurables.mapIndexed { index, measurable ->
            measurable.measure(
                constraints.copy(
                    maxHeight = if (index <= 6) singleWidth / 2 else singleWidth,
                    maxWidth = singleWidth,
                ),
            )
        }

        val calendarLayoutHeight =
            if (placeables.size != 42) {
                currentY + singleWidth + verticalGap
            } else {
                currentY + verticalGap
            }
        layout(
            width = constraints.maxWidth,
            height = calendarLayoutHeight,
        ) {
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(
                    x = xPos[index],
                    y = yPos[index],
                )
            }
        }
    }
}

@Composable
fun WorkoutHistoryCalendarView(
    modifier: Modifier = Modifier,
    monthCalendarData: MonthCalendarData,
    onClick: (DayCalendarData) -> Unit,
) {
    val month = monthCalendarData.month
    val monthYearLabel = remember {
        month.month.name.lowercase().replaceFirstChar { it.uppercaseChar() } +
            " " + month.year
    }
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = monthYearLabel,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        if (!monthCalendarData.dailyDataList.isEmpty()) {
            EmojiCalendarGrid(
                modifier = Modifier.padding(horizontal = 16.dp),
                monthCalendarData = monthCalendarData,
                onClick = onClick,
            )
        }
    }
}
