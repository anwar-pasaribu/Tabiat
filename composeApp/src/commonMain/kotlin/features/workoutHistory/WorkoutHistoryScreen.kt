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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import features.workoutHistory.model.MonthCalendarData
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.compose.koinInject


@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun WorkoutHistoryScreen(
    contentPadding: PaddingValues,
    hazeState: HazeState,
) {
    var showSheet by remember { mutableStateOf(false) }

    val viewModel = koinInject<WorkoutHistoryScreenViewModel>()
    val historyUiState by viewModel.uiStateMutableStateFlow.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCalenderData()
    }

    if (showSheet) {
        ExerciseLogListBottomSheet(
            targetDateTimeStamp = viewModel.selectedTimeStamp.collectAsState().value,
            onDismiss = {
                showSheet = false
            },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

        val paddingValues = PaddingValues(
            start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
            top = contentPadding.calculateTopPadding() + 16.dp,
            end = contentPadding.calculateEndPadding(LayoutDirection.Ltr),
            bottom = contentPadding.calculateBottomPadding() + WindowInsets.systemBars.asPaddingValues()
                .calculateBottomPadding(),
        )

        AnimatedContent(
            targetState = historyUiState,
            transitionSpec = { (fadeIn()).togetherWith(fadeOut()) },
        ) { uiState ->
            when (uiState) {
                WorkoutHistoryUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator(Modifier.size(32.dp))
                    }
                }

                is WorkoutHistoryUiState.Success -> {
                    CalendarContent(
                        calendarList = uiState.calendarItems,
                        contentPadding = paddingValues,
                        hazeState = hazeState,
                        onCalendarDayClick = {
                            showSheet = true
                            viewModel.setSelectedDate(it)
                        },
                    )
                }
            }
        }

        Spacer(
            Modifier.fillMaxWidth().height(contentPadding.calculateTopPadding()).hazeChild(
                state = hazeState,
                style = HazeMaterials.thick(MaterialTheme.colorScheme.background),
            ).align(Alignment.TopCenter).background(Color.Transparent)
        )
    }
}

@Composable
fun CalendarContent(
    modifier: Modifier = Modifier,
    calendarList: List<MonthCalendarData>,
    onCalendarDayClick: (LocalDate) -> Unit,
    contentPadding: PaddingValues,
    hazeState: HazeState,
) {
    val lazyColumnListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).month
        val itemPos = calendarList.indexOfFirst { it.month.month == today }
        lazyColumnListState.scrollToItem(itemPos)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().haze(state = hazeState).then(modifier),
        state = lazyColumnListState,
        contentPadding = contentPadding,
    ) {
        items(
            items = calendarList,
            key = { it.month.monthNumber },
        ) { calendarItem ->
            WorkoutHistoryCalendarView(
                monthCalendarData = calendarItem,
                onClick = { selectedDate ->
                    onCalendarDayClick(selectedDate.day)
                },
            )
        }
    }
}
