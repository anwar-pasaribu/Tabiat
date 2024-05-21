package features.workoutPlanDetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import domain.model.gym.ExerciseProgress
import getScreenSizeInfo
import org.koin.compose.koinInject
import ui.component.DeleteIconButton
import ui.component.EmptyState
import ui.component.gym.ExerciseFinishingStatusView
import ui.component.gym.WorkoutExerciseItemView

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun WorkoutDetailScreen(
    workoutPlanId: Long,
    onBack: () -> Unit = {},
    onNewExerciseToWorkoutPlan: () -> Unit = {},
    onSelectExercise: (exerciseId: Long) -> Unit = {},
) {

    var editMode by remember { mutableStateOf(false) }
    val hazeState = remember { HazeState() }
    val lazyListState = rememberLazyListState()

    val viewModel = koinInject<WorkoutDetailScreenViewModel>()
    val listItem by viewModel.exerciseListStateFlow.collectAsState()
    val workoutPlanItem by viewModel.workoutPlanStateFlow.collectAsState()

    val animateAlphaValue by animateFloatAsState(
        targetValue = if (editMode) 0f else 1f,
        label = "animateAlphaValue"
    )

    LaunchedEffect(workoutPlanId) {
        viewModel.loadWorkoutPlanById(workoutPlanId)
        viewModel.loadWorkoutPlan(workoutPlanId)
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
                    navigationIcon = {
                        IconButton(
                            modifier = Modifier.alpha(animateAlphaValue).scale(animateAlphaValue),
                            enabled = !editMode,
                            onClick = { onBack() },
                            content = {
                                Icon(
                                    painter = rememberVectorPainter(
                                        image = Icons.AutoMirrored.Filled.ArrowBack
                                    ),
                                    contentDescription = "Back"
                                )
                            }
                        )
                    },
                    title = {
                        Text(
                            "",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        IconButton(
                            modifier = Modifier.alpha(animateAlphaValue).scale(animateAlphaValue),
                            enabled = !editMode,
                            onClick = { onNewExerciseToWorkoutPlan() }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "")
                        }
                        IconButton(onClick = { editMode = !editMode }) {
                            Icon(
                                imageVector = if (editMode) Icons.Default.Close else Icons.Outlined.Edit,
                                contentDescription = ""
                            )
                        }
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
        },
    ) { contentPadding ->

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
            stickyHeader(contentType = "exercises") {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = workoutPlanItem?.name.orEmpty(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
            if (listItem.isEmpty()) {
                editMode = false
                item {
                    Column(modifier = Modifier.fillMaxSize()) {
                        EmptyState(
                            title = "Belum ada latihan yang ditambahkan",
                            btnText = "Tambah Latihan",
                            onClick = {
                                onNewExerciseToWorkoutPlan()
                            }
                        )
                    }
                }
            }
            items(
                items = listItem,
                key = { item -> item.exercise.id },
                contentType = { "exercises" }
            ) { item: ExerciseProgress ->
                Box(
                    modifier = Modifier.fillMaxWidth().animateItemPlacement(),
                ) {

                    AnimatedVisibility(
                        visible = editMode,
                        modifier = Modifier.width(48.dp).align(Alignment.CenterEnd),
                        enter = scaleIn(),
                        exit = scaleOut(animationSpec = tween(150, delayMillis = 300))
                    ) {
                        Row {
                            Spacer(Modifier.width(8.dp))
                            DeleteIconButton(
                                onClick = {
                                    viewModel.deleteExercise(workoutPlanId, item.exercise.id)
                                }
                            )
                        }
                    }

                    val screenSizeInfo = getScreenSizeInfo()
                    val exerciseCardWidth = screenSizeInfo.wDP
                    val animatedWidth by animateDpAsState(
                        targetValue =
                        if (editMode) exerciseCardWidth - (16.dp + 48.dp)
                        else exerciseCardWidth - (16.dp),
                        label = "exerciseCardWidth"
                    )
                    WorkoutExerciseItemView(
                        modifier = Modifier.width(animatedWidth),
                        title = item.exercise.name,
                        description = item.exercise.description,
                        imageUrlList = item.exercise.imageList,
                        enabled = !editMode,
                        onClick = {
                            onSelectExercise(item.exercise.id)
                        },
                        progressContentView = {
                            ExerciseFinishingStatusView(
                                total = item.sessionTotal,
                                progress = item.sessionDoneCount
                            )
                        }
                    )
                }
            }
            item {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
            }
        }
    }
}