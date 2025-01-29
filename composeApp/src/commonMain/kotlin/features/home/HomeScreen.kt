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
package features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mohamedrejeb.calf.permissions.ExperimentalPermissionsApi
import com.mohamedrejeb.calf.permissions.Permission
import com.mohamedrejeb.calf.permissions.isNotGranted
import com.mohamedrejeb.calf.permissions.rememberPermissionState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import features.home.model.HomeListItemUiData
import features.home.model.HomeWeeklyUiData
import features.workoutHistory.ExerciseLogListBottomSheet
import kotlinx.datetime.LocalDate
import org.koin.compose.koinInject
import ui.component.EmptyState
import ui.component.InsetNavigationHeight
import ui.component.calendar.WeekView
import ui.component.card.NotificationPermissionStatusCard
import ui.component.gym.LatestExercise
import ui.component.gym.WorkoutPlanItemView

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onWorkoutDetail: (HomeListItemUiData) -> Unit = {},
    onEditWorkout: (Long) -> Unit = {},
    openHistoryScreen: () -> Unit = {},
    onCreateNewWorkoutPlan: () -> Unit = {},
    paddingValues: PaddingValues,
    hazeState: HazeState,
) {
    val viewModel = koinInject<HomeScreenViewModel>()
    val homeScreenUiState by viewModel.workoutListStateFlow.collectAsState()
    val homeWeeklyData by viewModel.weeklyDataListStateFlow.collectAsState()

    var selectedDateTimeStamp by remember { mutableStateOf(0L) }
    var dailyExerciseLogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadWorkoutList()
    }

    if (dailyExerciseLogVisible) {
        ExerciseLogListBottomSheet(
            targetDateTimeStamp = selectedDateTimeStamp,
            onDismiss = {
                dailyExerciseLogVisible = false
            },
        )
    }

    HomeScreenList(
        modifier = Modifier.fillMaxSize(),
        contentPadding = paddingValues,
        homeScreenUiState = homeScreenUiState,
        hazeState = hazeState,
        homeWeeklyData = homeWeeklyData,
        onWorkoutDetail = onWorkoutDetail,
        onEditWorkout = onEditWorkout,
        onDeleteWorkout = {
            viewModel.deleteWorkout(it)
        },
        openHistoryScreen = {
            openHistoryScreen.invoke()
        },
        openDailyExercise = {
            selectedDateTimeStamp = viewModel.getAllDayTimeStamp(it)
            dailyExerciseLogVisible = true
        },
        onCreateNewWorkoutPlan = {
            onCreateNewWorkoutPlan.invoke()
        },
        onChangeWorkoutPlanColor = { workoutPlanId, colorHex ->
            viewModel.changeWorkoutPlanColor(workoutPlanId, colorHex)
        }
    )
}

@Composable
private fun HomeLoadingIndicator(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = .1F),
            ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp),
            )
        }
    }
}

@Composable
private fun HomeEmptyState(modifier: Modifier = Modifier, onCta: () -> Unit) {
    Box(modifier = modifier) {
        EmptyState(
            modifier = Modifier.fillMaxWidth(),
            title = "Belum ada Rencana Workout",
            btnText = "Tambah Workout",
            onClick = {
                onCta()
            },
        )
    }
}

@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreenList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    homeWeeklyData: List<HomeWeeklyUiData> = emptyList(),
    homeScreenUiState: HomeScreenUiState,
    hazeState: HazeState,
    onWorkoutDetail: (HomeListItemUiData) -> Unit,
    onEditWorkout: (Long) -> Unit,
    onDeleteWorkout: (Long) -> Unit,
    onChangeWorkoutPlanColor: (Long, String) -> Unit,
    openHistoryScreen: () -> Unit,
    openDailyExercise: (LocalDate) -> Unit,
    onCreateNewWorkoutPlan: () -> Unit,
) {

    val lazyListState: LazyListState = rememberLazyListState()

    val notificationCardDismissed = remember {
        mutableStateOf(false)
    }

    val launchNotificationRequest = remember {
        mutableStateOf(false)
    }

    val notificationPermissionState = rememberPermissionState(
        Permission.Notification
    )

    LaunchedEffect(launchNotificationRequest.value) {
        if (launchNotificationRequest.value) {
            notificationPermissionState.launchPermissionRequest()
        }
    }

    Box(modifier = modifier) {
        Column(
            Modifier
                .padding(
                    start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = contentPadding.calculateEndPadding(LayoutDirection.Ltr)
                )
                .hazeChild(
                    state = hazeState,
                    style = HazeMaterials.regular(MaterialTheme.colorScheme.background),
                )
                .align(Alignment.TopCenter)
                .zIndex(1F)
        ) {
            Spacer(Modifier.height(contentPadding.calculateTopPadding()))
            WeekView(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                listWeeklyUiData = homeWeeklyData,
                onMonthNameClick = {
                    openHistoryScreen.invoke()
                },
                onWeekDayClick = {
                    openDailyExercise.invoke(it)
                },
            )
            Spacer(Modifier.height(8.dp))
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().haze(state = hazeState),
            state = lazyListState,
            contentPadding = PaddingValues(
                start = contentPadding.calculateLeftPadding(LayoutDirection.Ltr) + 8.dp,
                top = contentPadding.calculateTopPadding() + 80.dp,
                end = contentPadding.calculateRightPadding(LayoutDirection.Ltr) + 8.dp,
                bottom = 0.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (notificationPermissionState.status.isNotGranted
                && !notificationCardDismissed.value
                && homeScreenUiState is HomeScreenUiState.Success) {
                item {
                    NotificationPermissionStatusCard(
                        onClick = {
                            launchNotificationRequest.value = true
                        },
                        onDismiss = {
                            notificationCardDismissed.value = true
                        }
                    )
                }
            }
            when (homeScreenUiState) {
                HomeScreenUiState.Empty -> {
                    item {
                        HomeEmptyState(Modifier.fillMaxWidth()) {
                            onCreateNewWorkoutPlan()
                        }
                    }
                }

                HomeScreenUiState.Loading -> {
                    item {
                        HomeLoadingIndicator(Modifier.fillMaxWidth())
                    }
                }

                is HomeScreenUiState.Success -> {
                    items(
                        items = homeScreenUiState.data,
                        key = { it.workoutPlanId }
                    ) { item ->
                        val colorTheme = item.backgroundColor.takeOrElse {
                            MaterialTheme.colorScheme.primary
                        }
                        WorkoutPlanItemView(
                            modifier = Modifier.fillMaxWidth().animateItem(),
                            workoutPlanId = item.workoutPlanId,
                            title = item.title,
                            description = item.description,
                            lastActivityInfo = {
                                if (item.lastActivityDetail.isNotEmpty()) {
                                    LatestExercise(
                                        modifier = Modifier.padding(
                                            start = 10.dp,
                                            bottom = 10.dp
                                        ),
                                        backgroundColor = colorTheme,
                                        exerciseImageUrl = item.exerciseImageUrl,
                                        upperLabel = item.lastActivityDate,
                                        lowerLabel = item.lastActivityDetail,
                                    )
                                }
                            },
                            total = item.total,
                            progress = item.progress,
                            backgroundColor = colorTheme,
                            onClick = { onWorkoutDetail(item) },
                            onEditRequest = { onEditWorkout(item.workoutPlanId) },
                            onDeleteRequest = { onDeleteWorkout(item.workoutPlanId) },
                            onChangeColorRequest = {
                                onChangeWorkoutPlanColor.invoke(
                                    item.workoutPlanId,
                                    it
                                )
                            },
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
