package features.workoutPlanDetail

import PlayHapticAndSound
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import domain.model.gym.Exercise
import features.inputExercise.WorkoutDetailScreenViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject
import ui.component.gym.ExerciseListItemView

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class
)
@Composable
fun WorkoutDetailScreen(
    workoutPlanId: Long,
    onBack: () -> Unit = {},
    onNewExerciseToWorkoutPlan: () -> Unit = {},
    onSelectExercise: (exerciseId: Long) -> Unit = {},
) {

    val hazeState = remember { HazeState() }
    val selectedEmojiUnicodes = remember { mutableStateListOf("") }
    var selectedEmojiUnicode by remember { mutableStateOf("") }
    var selectedEmojiOffset by remember { mutableStateOf(Offset.Zero) }
    val lazyListState = rememberLazyListState()
    val selectedEmojiUnicodeAndOffset =
        remember { mutableStateOf(MutableStateFlow(Pair("", Offset.Zero))) }
    var selectedDateTimeStamp by remember { mutableStateOf(0L) }
    var moodStateBottomSheetStateShowed by remember { mutableStateOf(false) }

    val viewModel = koinInject<WorkoutDetailScreenViewModel>()
    val listItem by viewModel.exerciseListStateFlow.collectAsState()

    LaunchedEffect(workoutPlanId) {
        viewModel.loadWorkoutPlan(workoutPlanId)
    }

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
                            "Workout Detail",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                onNewExerciseToWorkoutPlan()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create new Workout Plan"
                            )
                        }
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
                items(items = listItem) { item: Exercise ->

                    ExerciseListItemView(
                        title = item.name,
                        description = item.description,
                        onClick = {
                            onSelectExercise(item.id)
                        }
                    )
                }
                item {
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
                }
            }
        }
    }
}