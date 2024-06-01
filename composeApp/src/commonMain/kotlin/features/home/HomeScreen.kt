package features.home

import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import features.home.model.HomeListItemUiData
import features.settings.SettingBottomSheetDialog
import features.workoutHistory.ExerciseLogListBottomSheet
import org.koin.compose.koinInject
import tabiat.composeapp.generated.resources.Res
import tabiat.composeapp.generated.resources.tabiat_icon_32dp
import ui.component.EmptyState
import ui.component.ImageWrapper
import ui.component.InsetNavigationHeight
import ui.component.calendar.WeekView
import ui.component.gym.LatestExercise
import ui.component.gym.WorkoutPlanItemView

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class
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
    val homeScreenUiState by viewModel.workoutListStateFlow.collectAsState()

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
                        ImageWrapper(
                            resource = Res.drawable.tabiat_icon_32dp,
                            contentDescription = ""
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { settingScreenVisible = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Menu,
                                contentDescription = "Setting Menu"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            modifier = Modifier.size(30.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            onClick = { onCreateNewWorkoutPlan() }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "")
                        }
                        Spacer(Modifier.width(8.dp))
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

        val paddingValues = PaddingValues(
            start = contentPadding.calculateStartPadding(LayoutDirection.Ltr) + 8.dp,
            top = contentPadding.calculateTopPadding() + 16.dp,
            end = contentPadding.calculateEndPadding(LayoutDirection.Ltr) + 8.dp,
            bottom = contentPadding.calculateBottomPadding()
        )

        when (val uiState = homeScreenUiState) {
            is HomeScreenUiState.Loading -> {
                HomeLoadingIndicator(Modifier.fillMaxSize().padding(paddingValues))
            }

            is HomeScreenUiState.Error -> {
                HomeErrorIndicator(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    onCta = { onCreateNewWorkoutPlan() }
                )
            }

            is HomeScreenUiState.Success -> {
                HomeScreenList(
                    contentPadding = paddingValues,
                    listItem = uiState.data,
                    hazeState = hazeState,
                    lazyListState = lazyListState,
                    onWorkoutDetail = onWorkoutDetail,
                    onEditWorkout = onEditWorkout,
                    onDeleteWorkout = {
                        viewModel.deleteWorkout(it)
                    }
                )
            }

            HomeScreenUiState.Empty -> {
                HomeEmptyState(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    onCta = { onCreateNewWorkoutPlan() }
                )
            }
        }

    }
}

@Composable
private fun HomeLoadingIndicator(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = .1F),
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
            )
        }
    }
}

@Composable
private fun HomeErrorIndicator(modifier: Modifier = Modifier, onCta: () -> Unit) {
    Box(modifier = modifier) {
        EmptyState(
            modifier = Modifier.fillMaxWidth(),
            title = "Ada error nih",
            btnText = "Coba Tambah Workout",
            onClick = {
                onCta()
            }
        )
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
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    listItem: List<HomeListItemUiData>,
    hazeState: HazeState,
    lazyListState: LazyListState,
    onWorkoutDetail: (Long) -> Unit,
    onEditWorkout: (Long) -> Unit,
    onDeleteWorkout: (Long) -> Unit,
) {
    LazyColumn(
        modifier = modifier.then(Modifier.fillMaxSize().haze(state = hazeState)),
        state = lazyListState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = listItem, key = { it.workoutPlanId }) { item ->
            WorkoutPlanItemView(
                modifier = Modifier.fillMaxWidth().animateItemPlacement(
                    animationSpec = tween(150, delayMillis = 600)
                ),
                title = item.title,
                description = item.description,
                total = item.total,
                progress = item.progress,
                onClick = { onWorkoutDetail(item.workoutPlanId) },
                onEditRequest = { onEditWorkout(item.workoutPlanId) },
                onDeleteRequest = { onDeleteWorkout(item.workoutPlanId) },
                lastActivityInfo = {
                    if (item.lastActivityDetail.isNotEmpty()) {
                        LatestExercise(
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                            exerciseImageUrl = item.exerciseImageUrl,
                            upperLabel = item.lastActivityDate,
                            lowerLabel = item.lastActivityDetail
                        )
                    }
                }
            )
        }

        item {
            InsetNavigationHeight()
        }
    }
}