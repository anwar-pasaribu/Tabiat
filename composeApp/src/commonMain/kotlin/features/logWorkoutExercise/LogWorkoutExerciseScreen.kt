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
package features.logWorkoutExercise

import PlayHapticAndSound
import SendNotification
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope.PlaceHolderSize.Companion.animatedSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import domain.model.gym.ExerciseSet
import org.koin.compose.koinInject
import platform.BackHandler
import platform.PlaySoundEffect
import ui.component.DeleteIconButton
import ui.component.EditIconButton
import ui.component.MainHeaderText
import ui.component.PagerIndicator
import ui.component.gym.AddExerciseSet
import ui.component.gym.ExerciseAxisData
import ui.component.gym.ExerciseProgressLineChart
import ui.component.gym.ExerciseSetItemView
import ui.component.gym.ImagePager
import ui.component.gym.TimerDisplay
import ui.extension.dummyClickable

sealed class LogWorkoutExerciseUiState {
    data object Default : LogWorkoutExerciseUiState()
    data object LoggerView : LogWorkoutExerciseUiState()
    data object EditMode : LogWorkoutExerciseUiState()
}

sealed class TimerState {
    data object NoTimer : TimerState()
    data object SetTimer : TimerState()
    data object BreakTimer : TimerState()
}

@Composable
fun LogWorkoutExerciseScreen(
    contentPadding: PaddingValues,
    workoutPlanId: Long,
    exerciseId: Long,
    onBack: () -> Unit = {},
) {

    val hazeState = remember { HazeState() }

    var uiState by remember { mutableStateOf<LogWorkoutExerciseUiState>(LogWorkoutExerciseUiState.Default) }
    var timerState by remember { mutableStateOf<TimerState>(TimerState.NoTimer) }

    var editMode by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()

    var selectedWorkoutPlanExerciseId by remember { mutableStateOf(0L) }
    var selectedExerciseSet by remember { mutableStateOf(ExerciseSet(0, 0, 0)) }
    var logExerciseSpinnerVisible by remember { mutableStateOf(false) }

    val viewModel = koinInject<LogWorkoutExerciseScreenViewModel>()
    var exerciseSetTimerDuration by remember { mutableStateOf(0) }
    var breakTimeDuration by remember { mutableStateOf(0) }

    val animateAlphaValue by animateFloatAsState(
        targetValue = if (editMode) 0f else 1f,
        label = "animateAlphaValue",
    )

    val gymPreferences by viewModel.gymPreferences.collectAsState()
    val exerciseSetList by viewModel.exerciseSetList.collectAsState()
    val exerciseLogs by viewModel.exerciseLogs.collectAsState()
    val exerciseName by viewModel.exerciseName.collectAsState()
    val exerciseImages by viewModel.exercisePics.collectAsState()
    val allExerciseFinished by viewModel.allExerciseSetFinished.collectAsState(false)

    val chartXAxisData = remember { mutableStateOf(emptyList<Double>()) }
    val chartYAxisData = remember { mutableStateOf(emptyList<Long>()) }

    LaunchedEffect(gymPreferences) {
        exerciseSetTimerDuration = gymPreferences.setTimerDuration
        breakTimeDuration = gymPreferences.breakTimeDuration
    }

    LaunchedEffect(exerciseLogs) {
        chartXAxisData.value = exerciseLogs.map { it.weight }
        chartYAxisData.value = exerciseLogs.map { it.dateTimestamp }
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

    val topAppBarAlphaDuringTimer =
        if (timerState !is TimerState.NoTimer || logExerciseSpinnerVisible) {
            0F
        } else {
            1F
        }

    BackHandler {
        when (uiState) {
            LogWorkoutExerciseUiState.Default -> onBack()
            LogWorkoutExerciseUiState.LoggerView -> {
                logExerciseSpinnerVisible = false
                uiState = LogWorkoutExerciseUiState.Default
            }

            LogWorkoutExerciseUiState.EditMode -> {
                editMode = false
                uiState = LogWorkoutExerciseUiState.Default
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val imagePagerHeight = 240.dp

        val contentTopPadding = if (exerciseImages.isEmpty()) {
            contentPadding.calculateTopPadding()
        } else {
            imagePagerHeight + 8.dp
        }

        Column(
            modifier = Modifier.padding(
                start = contentPadding.calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                top = contentTopPadding,
                end = contentPadding.calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
                bottom = contentPadding.calculateBottomPadding() + 166.dp,
            ),
        ) {
            Box(modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
                Row(modifier = Modifier.align(Alignment.CenterStart)) {
                    MainHeaderText(
                        modifier = Modifier.padding(end = 40.dp),
                        textTitle = if (editMode) "Edit" else exerciseName,
                    )
                }

                Row(
                    modifier = Modifier.width(40.dp).align(Alignment.CenterEnd),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    EditIconButton(
                        modifier = Modifier.size(32.dp),
                        editMode = !editMode,
                        onEditClick = {
                            editMode = true
                            uiState = LogWorkoutExerciseUiState.EditMode
                        },
                        onCancelClick = {
                            editMode = false
                            uiState = LogWorkoutExerciseUiState.Default
                        }
                    )
                }
            }
            Card(modifier = Modifier.padding(vertical = 16.dp)) {
                LazyColumn(state = lazyListState) {
                    items(
                        items = exerciseSetList,
                        key = { it.workoutPlanExerciseId },
                    ) { item ->
                        Modifier.fillMaxWidth()
                        Box(modifier = Modifier.animateItem()) {
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
                                    selectedWorkoutPlanExerciseId =
                                        item.workoutPlanExerciseId
                                    selectedExerciseSet =
                                        ExerciseSet(
                                            item.setOrder,
                                            item.repsCount,
                                            item.weight,
                                        )
                                },
                                onSetItemLongClick = {
                                    editMode = true
                                    uiState = LogWorkoutExerciseUiState.EditMode
                                },
                            )
                            androidx.compose.animation.AnimatedVisibility(
                                visible = editMode,
                                modifier = Modifier.padding(end = 8.dp).width(48.dp)
                                    .align(Alignment.CenterEnd),
                                enter = scaleIn(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMediumLow,
                                    ),
                                ),
                                exit = scaleOut(animationSpec = tween(150)),
                            ) {
                                Row {
                                    Spacer(Modifier.width(8.dp))
                                    DeleteIconButton(
                                        onClick = {
                                            viewModel.deleteExerciseSet(
                                                workoutPlanId = workoutPlanId,
                                                exerciseId = exerciseId,
                                                selectedWorkoutPlanExerciseId = item.workoutPlanExerciseId,
                                            )
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        val axisData = ExerciseAxisData(
            title = exerciseName,
            xAxis = chartXAxisData.value,
            yAxis = chartYAxisData.value,
        )

        // Chart
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(bottom = contentPadding.calculateBottomPadding()),
        ) {
            ExerciseProgressLineChart(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                axisData,
            )
        }

        // Image Pager Below Log Exercise UI
        if (exerciseImages.isNotEmpty()) {
            ExerciseImagePager(
                modifier = Modifier.fillMaxWidth().height(imagePagerHeight),
                imageUrlList = exerciseImages,
                paddingValues = contentPadding
            )
        }

        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = timerState,
            contentAlignment = Alignment.Center,
            transitionSpec = { EnterTransition.None togetherWith ExitTransition.None }
        ) { targetTimerState ->
            when (targetTimerState) {
                TimerState.NoTimer -> {}
                TimerState.SetTimer -> {
                    TimerDisplay(
                        countDown = exerciseSetTimerDuration,
                        breakTime = false,
                        onTimerFinished = {
                            timerState = if (allExerciseFinished && (breakTimeDuration != 0)) {
                                TimerState.BreakTimer
                            } else {
                                TimerState.NoTimer
                            }
                            showNotification = true
                        },
                        onCancelTimer = {
                            timerState = TimerState.NoTimer
                            viewModel.saveRunningTimer(0)
                        },
                        onMinimizeTimer = { timeLeft ->
                            timerState = TimerState.NoTimer
                            viewModel.saveRunningTimer(timeLeft)
                        }
                    )
                }

                TimerState.BreakTimer -> {
                    TimerDisplay(
                        countDown = breakTimeDuration,
                        breakTime = true,
                        onTimerFinished = {
                            showNotification = true
                            onBack()
                        },
                        onCancelTimer = {
                            timerState = TimerState.NoTimer
                            viewModel.saveRunningTimer(0)
                        },
                        onMinimizeTimer = { timeLeft ->
                            timerState = TimerState.NoTimer
                            viewModel.saveRunningTimer(timeLeft)
                        }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = logExerciseSpinnerVisible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .dummyClickable()
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.inverseSurface.copy(alpha = .85F))
                    .zIndex(99F),
                contentAlignment = Alignment.Center,
            ) {
                Column {
                    IconButton(
                        modifier = Modifier.align(Alignment.Start).padding(start = 4.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        onClick = {
                            logExerciseSpinnerVisible = false
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "",
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
                                weight = weight,
                            )
                            logExerciseSpinnerVisible = false
                            showNotification = false
                            when {
                                exerciseSetTimerDuration == 0 && breakTimeDuration == 0 -> {
                                    timerState = TimerState.NoTimer
                                }

                                allExerciseFinished && breakTimeDuration != 0 -> {
                                    timerState = TimerState.BreakTimer
                                }

                                !allExerciseFinished && exerciseSetTimerDuration != 0 -> {
                                    timerState = TimerState.SetTimer
                                }

                                else -> {
                                    timerState = TimerState.NoTimer
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}

// Material 3 Emphasized Easing
// https://m3.material.io/styles/motion/easing-and-duration/tokens-specs

private const val DURATION = 600
private const val DURATION_ENTER = 400
private const val DURATION_EXIT = 200

sealed class ImagePagerState {
    data object Normal: ImagePagerState()
    data object Expanded: ImagePagerState()
}

@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ExerciseImagePager(
    modifier: Modifier = Modifier,
    imageUrlList: List<String>,
    paddingValues: PaddingValues
) {
    var imagePagerExpandState: ImagePagerState by remember {
        mutableStateOf(ImagePagerState.Normal)
    }
    val hazeState = remember { HazeState() }

    var selectedItem by remember { mutableIntStateOf(0) }

    val pagerState1 = rememberPagerState(initialPage = selectedItem) { imageUrlList.size }
    val pagerState2 = rememberPagerState(initialPage = selectedItem) { imageUrlList.size }

    BackHandler(enabled = (imagePagerExpandState is ImagePagerState.Expanded)) {
        if (imagePagerExpandState is ImagePagerState.Expanded) {
            imagePagerExpandState = ImagePagerState.Normal
        }
    }

    Box(modifier = Modifier.background(Color.Black)) {
        SharedTransitionLayout {
            AnimatedContent(
                targetState = imagePagerExpandState,
                label = "animated-pager-expand",
            ) { pagerUiState ->

                when (pagerUiState) {
                    ImagePagerState.Normal -> {
                        Box(modifier = Modifier
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "image-container-bounds"),
                                animatedVisibilityScope = this,
                            )
                            .fillMaxWidth()
                            .background(Color.Black)
                        ) {
                            ImagePager(
                                pagerStateAdapter = pagerState1,
                                modifier = Modifier
                                    .sharedElement(
                                        boundsTransform = { _, _ ->
                                            spring(
                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                stiffness = Spring.StiffnessMediumLow // with medium speed
                                            )
                                        },
                                        state = rememberSharedContentState(key = "imagePager"),
                                        animatedVisibilityScope = this@AnimatedContent,
                                        placeHolderSize = animatedSize,
                                    )
                                    .haze(hazeState).then(modifier),
                                imageUrlList = imageUrlList,
                                pageSpacing = 0.dp,
                                fillWidth = true,
                                contentScale = ContentScale.Crop,
                                shape = RectangleShape,
                                onPageChange = { currentPage ->
                                    selectedItem = currentPage
                                },
                                onItemClicked = {
                                    selectedItem = it
                                    imagePagerExpandState = ImagePagerState.Expanded
                                }
                            )

                            Box(
                                modifier = Modifier
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = "pager-indicator-bounds"),
                                        animatedVisibilityScope = this@AnimatedContent,
                                    )
                                    .padding(bottom = 6.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .hazeChild(
                                        state = hazeState,
                                        style = HazeMaterials.thin(),
                                    )
                                    .height(16.dp)
                                    .padding(horizontal = 2.dp)
                                    .align(Alignment.BottomCenter)
                            ) {
                                PagerIndicator(
                                    modifier = Modifier.align(Alignment.Center),
                                    pageCount = imageUrlList.size,
                                    activePage = selectedItem,
                                )
                            }
                        }
                    }

                    ImagePagerState.Expanded -> {
                        Box(
                            modifier = Modifier
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = "image-container-bounds"),
                                    animatedVisibilityScope = this,
                                )
                                .fillMaxSize()
                                .background(Color.Black)
                            ,
                            contentAlignment = Alignment.Center
                        ) {
                            ImagePager(
                                pagerStateAdapter = pagerState2,
                                modifier = Modifier
                                    .sharedElement(
                                        boundsTransform = { _, _ ->
                                            spring(
                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                stiffness = Spring.StiffnessMediumLow // with medium speed
                                            )
                                        },
                                        state = rememberSharedContentState(key = "imagePager"),
                                        animatedVisibilityScope = this@AnimatedContent,
                                        placeHolderSize = animatedSize,
                                    )
                                    .haze(hazeState)
                                ,
                                imageUrlList = imageUrlList,
                                pageSpacing = 0.dp,
                                fillWidth = true,
                                contentScale = ContentScale.FillWidth,
                                shape = RectangleShape,
                                onPageChange = { currentPage ->
                                    selectedItem = currentPage
                                },
                                onItemClicked = {
                                    imagePagerExpandState = ImagePagerState.Normal
                                }
                            )

                            Box(
                                modifier = Modifier
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = "pager-indicator-bounds"),
                                        animatedVisibilityScope = this@AnimatedContent,
                                    )
                                    .padding(bottom = paddingValues.calculateBottomPadding() + 16.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .hazeChild(
                                        state = hazeState,
                                        style = HazeMaterials.thin(),
                                    )
                                    .height(16.dp)
                                    .padding(horizontal = 2.dp)
                                    .align(Alignment.BottomCenter)
                            ) {
                                PagerIndicator(
                                    modifier = Modifier.align(Alignment.Center),
                                    pageCount = imageUrlList.size,
                                    activePage = selectedItem,
                                )
                            }
                        }
                    }
                }
            }
        }

//        Spacer(Modifier
//            .hazeChild(state = hazeState, style = HazeMaterials.regular(MaterialTheme.colorScheme.background)) {
//                progressive = HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
//            }
//            .background(Color.Transparent)
//            .fillMaxWidth()
//            .height(height = paddingValues.calculateTopPadding() - 16.dp)
//            .align(Alignment.TopCenter)
//        )
    }
}

//const val DURATION_EXTRA_LONG = 1000
//
//private val emphasizedPathContainer = Path().apply {
//    moveTo(0f, 0f)
//    cubicTo(0.05f, 0f, 0.133333f, 0.06f, 0.166666f, 0.04f)
//    cubicTo(0.208333f, 0.82f, 0.25f, 1f, 1f, 1f)
//}
//
//val emphasizedContainer = PathInterpolator(emphasizedPathContainer)
//
//val EmphasizedEasingContainer = Easing { emphasizedContainer.getInterpolation(it) }