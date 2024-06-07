package features.logWorkoutExercise

import PlayHapticAndSound
import SendNotification
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import domain.model.gym.ExerciseSet
import org.koin.compose.koinInject
import platform.BackHandler
import platform.PlaySoundEffect
import ui.component.BackButton
import ui.component.DeleteIconButton
import ui.component.PagerIndicator
import ui.component.gym.AddExerciseSet
import ui.component.gym.ExerciseSetItemView
import ui.component.gym.ImagePager
import ui.component.gym.TimerDisplay
import ui.extension.dummyClickable

sealed class LogWorkoutExerciseUiState {
    data object Default: LogWorkoutExerciseUiState()
    data object LoggerView: LogWorkoutExerciseUiState()
    data object EditMode: LogWorkoutExerciseUiState()
    data object SetTimer: LogWorkoutExerciseUiState()
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun LogWorkoutExerciseScreen(
    workoutPlanId: Long,
    exerciseId: Long,
    onBack: () -> Unit = {},
) {

    var uiState by remember { mutableStateOf<LogWorkoutExerciseUiState>(LogWorkoutExerciseUiState.Default) }

    var editMode by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()

    val hazeState = remember { HazeState() }
    var selectedWorkoutPlanExerciseId by remember { mutableStateOf(0L) }
    var selectedExerciseSet by remember { mutableStateOf(ExerciseSet(0, 0, 0)) }
    var logExerciseSpinnerVisible by remember { mutableStateOf(false) }
    var logExerciseTimerVisible by remember { mutableStateOf(false) }
    var breakTimerVisible by remember { mutableStateOf(false) }

    val viewModel = koinInject<LogWorkoutExerciseScreenViewModel>()
    val gymPreferences by viewModel.gymPreferences.collectAsState()
    var exerciseSetTimerDuration by remember { mutableStateOf(0) }
    var breakTimeDuration by remember { mutableStateOf(0) }

    val animateAlphaValue by animateFloatAsState(
        targetValue = if (editMode) 0f else 1f,
        label = "animateAlphaValue"
    )

    val exerciseSetList by viewModel.exerciseSetList.collectAsState()
    val exerciseName by viewModel.exerciseName.collectAsState()
    val exerciseImages by viewModel.exercisePics.collectAsState()
    val allExerciseFinished by viewModel.allExerciseSetFinished.collectAsState(false)

    LaunchedEffect(gymPreferences) {
        exerciseSetTimerDuration = gymPreferences.setTimerDuration
        breakTimeDuration = gymPreferences.breakTimeDuration
    }

    LaunchedEffect(workoutPlanId, exerciseId) {
        viewModel.getExerciseSetList(workoutPlanId, exerciseId)
        viewModel.getExerciseDetail(exerciseId)
    }

    if (selectedWorkoutPlanExerciseId != 0L) {
        PlayHapticAndSound(selectedWorkoutPlanExerciseId)
    }

    var showNotification by remember { mutableStateOf(false) }
    if (showNotification) {
        SendNotification("Timer Selesai", "Lanjut Latihan atau istirahat")
        PlaySoundEffect(Unit, gymPreferences.timerSoundEffect)
    }

    val topAppBarAlphaDuringTimer = if(logExerciseTimerVisible) {
        0F
    } else 1F

    BackHandler {
        when (uiState) {
            LogWorkoutExerciseUiState.Default -> onBack()
            LogWorkoutExerciseUiState.LoggerView -> {
                logExerciseSpinnerVisible = false
                uiState = LogWorkoutExerciseUiState.Default
            }
            is LogWorkoutExerciseUiState.SetTimer -> {
                onBack()
            }

            LogWorkoutExerciseUiState.EditMode -> {
                editMode = false
                uiState = LogWorkoutExerciseUiState.Default
            }
        }
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
                    modifier = Modifier.fillMaxWidth().alpha(topAppBarAlphaDuringTimer),
                    colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
                    navigationIcon = {
                        BackButton(
                            modifier = Modifier.alpha(animateAlphaValue).scale(animateAlphaValue),
                            enabled = !editMode,
                            onClick = { onBack() },
                            showBackground = true
                        )
                    },
                    title = { Text("") },
                    actions = {
                        IconButton(
                            onClick = {
                                editMode = !editMode
                                uiState = LogWorkoutExerciseUiState.EditMode
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        ) {
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

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val imagePagerHeight = 320.dp
            if (exerciseImages.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth()) {

                    var activePage by remember {
                        mutableStateOf(0)
                    }

                    ImagePager(
                        modifier = Modifier.fillMaxWidth().height(imagePagerHeight),
                        imageUrlList = exerciseImages,
                        pageSpacing = 0.dp,
                        fillWidth = true,
                        contentScale = ContentScale.Crop,
                        shape = RectangleShape,
                        onPageChange = { currentPage ->
                            activePage = currentPage
                        }
                    )

                    PagerIndicator(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp),
                        pageCount = exerciseImages.size,
                        activePage = activePage
                    )
                }
            }

            val contentTopPadding = if(exerciseImages.isEmpty()) {
                contentPadding.calculateTopPadding() + 16.dp
            } else {
                imagePagerHeight + 8.dp
            }
            Column(
                modifier = Modifier.padding(
                    start = contentPadding.calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                    top = contentTopPadding,
                    end = contentPadding.calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
                    bottom = contentPadding.calculateBottomPadding()
                )
            ) {
                Text(
                    text = exerciseName,
                    style = MaterialTheme.typography.headlineMedium
                )
                Card(
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    LazyColumn(
                        state = lazyListState,
                    ) {
                        items(
                            items = exerciseSetList,
                            key = { it.workoutPlanExerciseId }
                        ) { item ->
                            Box(modifier = Modifier.fillMaxWidth().animateItemPlacement()) {
                                ExerciseSetItemView(
                                    modifier = Modifier,
                                    enabled = !editMode,
                                    setNumber = item.setOrder,
                                    setCount = item.repsCount,
                                    setWeight = item.weight,
                                    finished = item.finished,
                                    onSetItemClick = {
                                        uiState = LogWorkoutExerciseUiState.LoggerView
                                        logExerciseSpinnerVisible = !logExerciseSpinnerVisible
                                        selectedWorkoutPlanExerciseId = item.workoutPlanExerciseId
                                        selectedExerciseSet =
                                            ExerciseSet(item.setOrder, item.repsCount, item.weight)
                                    },
                                    onSetItemLongClick = {
                                        editMode = true
                                        uiState = LogWorkoutExerciseUiState.EditMode
                                    }
                                )
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = editMode,
                                    modifier = Modifier.padding(end = 8.dp).width(48.dp).align(Alignment.CenterEnd),
                                    enter = scaleIn(
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        )
                                    ),
                                    exit = scaleOut(animationSpec = tween(150))
                                ) {
                                    Row {
                                        Spacer(Modifier.width(8.dp))
                                        DeleteIconButton(
                                            onClick = {
                                                viewModel.deleteExerciseSet(
                                                    workoutPlanId = workoutPlanId,
                                                    exerciseId = exerciseId,
                                                    selectedWorkoutPlanExerciseId = item.workoutPlanExerciseId
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = logExerciseTimerVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {

                Box(
                    modifier = Modifier
                        .dummyClickable()
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = .75F)),
                    contentAlignment = Alignment.Center
                ) {
                    val timerDuration = if (allExerciseFinished) {
                        exerciseSetTimerDuration + breakTimeDuration
                    } else {
                        exerciseSetTimerDuration
                    }
                    TimerDisplay(
                        countDown = timerDuration,
                        breakTime = allExerciseFinished,
                        onTimerFinished = {
                            logExerciseTimerVisible = false
                            showNotification = true
                        },
                        onCancelTimer = { timeLeft ->
                            logExerciseTimerVisible = false
                            viewModel.saveRunningTimer(timeLeft)
                        }
                    )
                }
            }

            AnimatedVisibility(
                visible = breakTimerVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {

                Box(
                    modifier = Modifier
                        .dummyClickable()
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = .75F)),
                    contentAlignment = Alignment.Center
                ) {
                    TimerDisplay(
                        countDown = breakTimeDuration,
                        breakTime = true,
                        onTimerFinished = {
                            onBack()
                        },
                        onCancelTimer = { timeLeft ->
                            viewModel.saveRunningTimer(timeLeft)
                            onBack()
                        }
                    )
                }
            }

            AnimatedVisibility(
                visible = logExerciseSpinnerVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .dummyClickable()
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = .75F)),
                    contentAlignment = Alignment.Center
                ) {

                    Column {
                        IconButton(
                            modifier = Modifier.align(Alignment.Start).padding(start = 4.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            onClick = {
                                logExerciseSpinnerVisible = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = ""
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        AddExerciseSet(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            actionText = "Selesaikan Set",
                            initialReps = selectedExerciseSet.reps,
                            initialWeight = selectedExerciseSet.weight,
                            addExerciseSetDone = { reps, weight ->
                                viewModel.logExercise(
                                    selectedWorkoutPlanExerciseId = selectedWorkoutPlanExerciseId,
                                    workoutPlanId = workoutPlanId,
                                    exerciseId = exerciseId,
                                    reps = reps,
                                    weight = weight
                                )
                                logExerciseSpinnerVisible = false
                                showNotification = false
                                logExerciseTimerVisible = true
                                uiState = LogWorkoutExerciseUiState.SetTimer
                            }
                        )
                    }
                }
            }
        }
    }
}
