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

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.compose.koinInject
import ui.extension.LocalNavAnimatedVisibilityScope
import ui.extension.LocalSharedTransitionScope
import ui.extension.tabiatDetailBoundsTransform


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun WorkoutHistoryScreen(
    contentPadding: PaddingValues,
) {

    val viewModel = koinInject<WorkoutHistoryScreenViewModel>()
    val historyUiState by viewModel.historyUiState.collectAsState()
    val hazeState = remember { HazeState() }

    LaunchedEffect(Unit) {
        viewModel.loadCalenderData()
    }

    var showSheet by remember { mutableStateOf(false) }
    if (showSheet) {
        ExerciseLogListBottomSheet(
            targetDateTimeStamp = viewModel.selectedTimeStamp.collectAsState().value,
            onDismiss = {
                showSheet = false
            },
        )
    }

    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No sharedTransitionScope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No animatedVisibilityScope found")

    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(
                        key = "calender-screen"
                    ),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = tabiatDetailBoundsTransform,
                )
                .fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CalendarContent(
                    modifier = Modifier.fillMaxSize().haze(hazeState),
                    historyUiState = historyUiState,
                    onCalendarDayClick = {
                        showSheet = true
                        viewModel.setSelectedDate(it)
                    },
                    contentPadding = contentPadding,
                )
                Spacer(
                    Modifier
                        .hazeChild(
                            state = hazeState,
                            style = HazeMaterials.thin(),
                        )
                        .fillMaxWidth()
                        .height(contentPadding.calculateTopPadding()).background(
                    Color.Transparent)
                )
            }
        }
    }
}

@Composable
fun CalendarContent(
    modifier: Modifier = Modifier,
    historyUiState: WorkoutHistoryUiState,
    onCalendarDayClick: (LocalDate) -> Unit,
    contentPadding: PaddingValues,
) {

    val lazyColumnListState = rememberLazyListState()

    LaunchedEffect(historyUiState) {
        if (historyUiState is WorkoutHistoryUiState.Success) {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val itemPos = historyUiState.calendarItems.indexOfFirst {
                it.month.year == today.year
                        && it.month.monthNumber == today.monthNumber
            }
            lazyColumnListState.scrollToItem(itemPos)
        }
    }

    LazyColumn(
        modifier = modifier,
        state = lazyColumnListState,
        contentPadding = PaddingValues(
            start = contentPadding.calculateLeftPadding(LayoutDirection.Ltr),
            top = contentPadding.calculateTopPadding(),
            end = contentPadding.calculateRightPadding(LayoutDirection.Ltr),
            bottom = contentPadding.calculateBottomPadding()
        ),
    ) {
        when (historyUiState) {
            WorkoutHistoryUiState.Loading -> {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator(Modifier.size(32.dp))
                    }
                }
            }

            is WorkoutHistoryUiState.Success -> {
                items(
                    items = historyUiState.calendarItems,
                    key = { it.month.toEpochDays() },
                ) { calendarItem ->
                    WorkoutHistoryCalendarView(
                        modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                        monthCalendarData = calendarItem,
                        onClick = { selectedDate ->
                            onCalendarDayClick(selectedDate.day)
                        },
                    )
                }
            }
        }
    }
}
