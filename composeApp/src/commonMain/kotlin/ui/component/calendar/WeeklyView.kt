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
package ui.component.calendar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import features.home.model.HomeWeeklyUiData
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

@Composable
fun WeekView(
    modifier: Modifier = Modifier,
    onMonthNameClick: () -> Unit,
    onWeekDayClick: (Long) -> Unit,
) {
    val tz = TimeZone.currentSystemDefault()
    val today = Clock.System.todayIn(tz)
    Surface(
        modifier = modifier,
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
        ) {
            val dateAtFirstOfWeek = today.minus(today.dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)
            val listOfDaysThisWeek = List(7) { index ->
                dateAtFirstOfWeek.plus(index, DateTimeUnit.DAY)
            }

            WeeklyViewGrid(
                upperLabel = today.year.toString(),
                lowerLabel = today.month.name.take(3),
                onClick = {
                    onMonthNameClick()
                },
            )

            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier.width(1.dp).height(32.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerHighest,
                        RoundedCornerShape(.5.dp),
                    )
                    .align(Alignment.CenterVertically),
            )
            Spacer(modifier = Modifier.width(10.dp))

            listOfDaysThisWeek.forEachIndexed { index, date ->
                val isToday = date.dayOfWeek.isoDayNumber == today.dayOfWeek.isoDayNumber
                val todayDayOfWeek = today.dayOfWeek.isoDayNumber
                val isFuture = date.dayOfWeek.isoDayNumber > todayDayOfWeek
                val dayOfMonth = date.dayOfMonth
                val upperLabel = date.dayOfWeek.name
                    .take(3).lowercase().replaceFirstChar { it.uppercaseChar() }
                val lowerLabel = dayOfMonth.toString()

                WeeklyViewGrid(
                    modifier = Modifier.alpha(if (isFuture) .4F else 1F),
                    upperLabel = upperLabel,
                    lowerLabel = lowerLabel,
                    isToday = isToday,
                    clickEnabled = !isFuture,
                    onClick = {
                        val nowTimeStamp = Clock.System.now().toLocalDateTime(tz).time
                        val dateTime = LocalDateTime(date = date, time = nowTimeStamp)
                        onWeekDayClick(dateTime.toInstant(tz).toEpochMilliseconds())
                    },
                )
                if (index != 6) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Composable
fun WeekView(
    modifier: Modifier = Modifier,
    listWeeklyUiData: List<HomeWeeklyUiData> = emptyList(),
    onMonthNameClick: () -> Unit,
    onWeekDayClick: (LocalDate) -> Unit,
) {
    Surface(
        modifier = modifier,
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
        ) {
            AnimatedContent(
                targetState = listWeeklyUiData.isNotEmpty(),
                transitionSpec = {
                    fadeIn().togetherWith(fadeOut())
                },
            ) { weeklyDataAvailable ->
                if (weeklyDataAvailable) {
                    Row {
                        val firstData = listWeeklyUiData.first()
                        WeeklyViewGrid(
                            upperLabel = firstData.upperLabel,
                            lowerLabel = firstData.lowerLabel,
                            onClick = {
                                onMonthNameClick()
                            },
                        )

                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier.width(1.dp).height(32.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainerHighest,
                                    RoundedCornerShape(.5.dp),
                                )
                                .align(Alignment.CenterVertically),
                        )
                        Spacer(modifier = Modifier.width(10.dp))

                        listWeeklyUiData.subList(1, listWeeklyUiData.size).forEachIndexed { index, item ->
                            WeeklyViewGrid(
                                modifier = Modifier.alpha(if (item.isFuture) .4F else 1F),
                                upperLabel = item.upperLabel,
                                lowerLabel = item.lowerLabel,
                                isToday = item.isToday,
                                hasDot = item.hasActivity,
                                clickEnabled = !item.isFuture,
                                onClick = {
                                    onWeekDayClick(item.date)
                                },
                            )
                            if (index != 6) {
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(64.dp).background(
                            MaterialTheme.colorScheme.surfaceContainerHigh,
                            RoundedCornerShape(MaterialTheme.shapes.medium.topEnd),
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.WeeklyViewGrid(
    modifier: Modifier = Modifier,
    upperLabel: String,
    lowerLabel: String,
    isToday: Boolean = false,
    hasDot: Boolean = false,
    clickEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier.then(
            Modifier
                .weight(1F)
                .clip(RoundedCornerShape(8.dp))
                .clickable(enabled = clickEnabled) {
                    onClick()
                },
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = upperLabel,
            style = MaterialTheme.typography.labelSmall,
        )
        Text(
            modifier = Modifier.wrapContentWidth(),
            text = lowerLabel,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.None,
                ),
            ),
            textAlign = TextAlign.Center,
        )
        if (isToday) {
            Box(
                modifier = Modifier.background(
                    Color.Red,
                    CircleShape,
                ).size(6.dp),
            )
        } else if (hasDot) {
            Box(
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.primary,
                    CircleShape,
                ).size(6.dp),
            )
        }
    }
}
