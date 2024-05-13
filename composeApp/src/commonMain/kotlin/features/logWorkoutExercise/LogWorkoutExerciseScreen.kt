package features.logWorkoutExercise

import PlayHapticAndSound
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import domain.model.gym.ExerciseSet
import org.koin.compose.koinInject
import ui.component.gym.AddExerciseSet
import ui.component.gym.ExerciseSetItemView
import ui.component.gym.InputWorkoutPlanExerciseView
import ui.component.gym.TimerDisplay

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

    val hazeState = remember { HazeState() }
    var selectedEmojiUnicode by remember { mutableStateOf("") }
    var selectedWorkoutPlanExerciseId by remember { mutableStateOf(0L) }
    var selectedExerciseSet by remember { mutableStateOf(ExerciseSet(0, 0, 0)) }
    var logExerciseSpinnerVisible by remember { mutableStateOf(false) }
    var logExerciseTimerVisible by remember { mutableStateOf(false) }

    val viewModel = koinInject<LogWorkoutExerciseScreenViewModel>()

    LaunchedEffect(workoutPlanId, exerciseId) {
        viewModel.getExerciseSetList(workoutPlanId, exerciseId)
        viewModel.getExerciseDetail(exerciseId)
    }

    val exerciseSetList by viewModel.exerciseSetList.collectAsState()
    val exerciseName by viewModel.exerciseName.collectAsState()

    if (selectedEmojiUnicode.isNotEmpty()) {
        PlayHapticAndSound(selectedEmojiUnicode)
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
                            "Log Workout Exercise",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
        },
    ) { contentPadding ->

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(
                    start = contentPadding.calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                    top = contentPadding.calculateTopPadding() + 16.dp,
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
                    LazyColumn(reverseLayout = false) {
                        items(items = exerciseSetList) { item ->
                            Column {
                                ExerciseSetItemView(
                                    modifier = Modifier.animateItemPlacement(),
                                    setNumber = item.setOrder,
                                    setCount = item.repsCount,
                                    setWeight = item.weight,
                                    finished = item.finished
                                ) {
                                    logExerciseSpinnerVisible = !logExerciseSpinnerVisible
                                    selectedWorkoutPlanExerciseId = item.workoutPlanExerciseId
                                    selectedExerciseSet =
                                        ExerciseSet(item.setOrder, item.repsCount, item.weight)
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
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            enabled = true,
                            onClick = {}
                        )
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = .75F)),
                    contentAlignment = Alignment.Center
                ) {
                    TimerDisplay(
                        countDown = 15,
                        onTimerFinished = {
                            logExerciseTimerVisible = false
                        },
                        onCancelTimer = {
                            logExerciseTimerVisible = false
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
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            enabled = true,
                            onClick = {}
                        )
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = .75F)),
                    contentAlignment = Alignment.Center
                ) {

                    Column {
                        IconButton(
                            modifier = Modifier.align(Alignment.Start),
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
                                logExerciseTimerVisible = true
                            }
                        )
                    }
                }
            }
        }
    }
}
