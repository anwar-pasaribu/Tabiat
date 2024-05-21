package features.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import features.settings.SettingBottomSheetDialog
import features.workoutHistory.ExerciseLogListBottomSheet
import org.koin.compose.koinInject
import ui.component.EmptyState
import ui.component.InsetNavigationHeight
import ui.component.calendar.WeekView
import ui.component.gym.WorkoutPlanItemView

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun HomeScreen(
    onWorkoutDetail: (Long) -> Unit = {},
    onEditWorkout: (Long) -> Unit = {},
    openHistoryScreen: () -> Unit = {},
    onCreateNewWorkoutPlan: () -> Unit = {},
) {

    val hazeState = remember { HazeState() }
    val lazyListState = rememberLazyListState()

    val viewModel = koinInject<HomeScreenViewModel>()
    val listItem by viewModel.workoutListStateFlow.collectAsState()

    var selectedDateTimeStamp by remember { mutableStateOf(0L) }
    var dailyExerciseLogVisible by remember { mutableStateOf(false) }
    var settingScreenVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadWorkoutList()
    }

    if (dailyExerciseLogVisible) {
        ExerciseLogListBottomSheet(
            targetDateTimeStamp = selectedDateTimeStamp,
            onDismiss = {
                dailyExerciseLogVisible = false
            }
        )
    }

    if (settingScreenVisible) {
        SettingBottomSheetDialog(
            onDismiss = { settingScreenVisible = false }
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().hazeChild(
                    state = hazeState,
                    style = HazeMaterials.regular(MaterialTheme.colorScheme.background)
                ).background(Color.Transparent)
            ) {
                CenterAlignedTopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
                    title = {
                        Text(
                            "Home",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    },
                    actions = {
                        IconButton(onClick = { onCreateNewWorkoutPlan() }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "")
                        }
                        IconButton(onClick = { settingScreenVisible = true }) {
                            Icon(imageVector = Icons.Outlined.Settings, contentDescription = "")
                        }
                    }
                )

                WeekView(modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    onMonthNameClick = {
                        openHistoryScreen()
                    },
                    onWeekDayClick = {
                        selectedDateTimeStamp = it
                        dailyExerciseLogVisible = true
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
        },
    ) { contentPadding ->

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().haze(state = hazeState),
                state = lazyListState,
                contentPadding = PaddingValues(
                    start = contentPadding.calculateStartPadding(LayoutDirection.Ltr) + 8.dp,
                    top = contentPadding.calculateTopPadding() + 16.dp,
                    end = contentPadding.calculateEndPadding(LayoutDirection.Ltr) + 8.dp,
                    bottom = contentPadding.calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items = listItem, key = { it.workoutPlan.id }) { item ->
                    Column(
                        modifier = Modifier.fillMaxWidth().animateItemPlacement(),
                    ) {
                        WorkoutPlanItemView(
                            title = item.workoutPlan.name,
                            description = item.workoutPlan.description,
                            total = item.total,
                            progress = item.progress,
                            onClick = { onWorkoutDetail(item.workoutPlan.id) },
                            onEditRequest = { onEditWorkout(item.workoutPlan.id) },
                            onDeleteRequest = { viewModel.deleteWorkout(item.workoutPlan.id) }
                        )
                    }
                }

                if (listItem.isEmpty()) {
                    item {
                        Column(modifier = Modifier.fillMaxSize()) {
                            EmptyState(
                                modifier = Modifier.fillMaxWidth(),
                                title = "Belum ada Rencana Workout",
                                btnText = "Tambah Workout",
                                onClick = {
                                    onCreateNewWorkoutPlan()
                                }
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
}