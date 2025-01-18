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
package features.workoutPlanDetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import domain.model.detail.DetailItemEntity
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import tabiat.composeapp.generated.resources.Res
import tabiat.composeapp.generated.resources.ic_plus_circle_icon_32dp
import ui.component.DeleteIconButton
import ui.component.EditIconButton
import ui.component.EmptyState
import ui.component.InsetNavigationHeight
import ui.component.MainHeaderText
import ui.component.colorPalette.parseHexToComposeColor
import ui.component.gym.ExerciseFinishingStatusView
import ui.component.gym.WorkoutExerciseItemView
import ui.component.gym.bestContrastColor
import ui.extension.LocalNavAnimatedVisibilityScope
import ui.extension.LocalSharedTransitionScope
import ui.extension.tabiatDetailBoundsTransform

@Composable
fun WorkoutDetailScreen(
    paddingValues: PaddingValues,
    workoutPlanId: Long,
    targetColorTheme: String = "",
    onBack: () -> Unit = {},
    onNewExerciseToWorkoutPlan: () -> Unit = {},
    onSelectExercise: (exerciseId: Long) -> Unit = {},
    onImageClick: (exerciseId: Long, imageUrl: String?) -> Unit = { _,_ -> },
) {

    val currentWorkoutPlanId by rememberSaveable {
        mutableStateOf(workoutPlanId)
    }
    val viewModel = koinInject<WorkoutDetailScreenViewModel>()
    val exerciseListState by viewModel.exerciseListStateFlow.collectAsState()
    val workoutPlanDetailState by viewModel.workoutPlanStateFlow.collectAsState()

    LaunchedEffect(key1 = currentWorkoutPlanId) {
        viewModel.loadWorkoutPlanDetails(currentWorkoutPlanId)
    }

    WorkoutDetailView(
        modifier = Modifier.fillMaxSize(),
        paddingValues = paddingValues,
        workoutPlanId = workoutPlanId,
        targetBackgroundColor = if (targetColorTheme.isEmpty()) {
            MaterialTheme.colorScheme.primary
        } else {
            targetColorTheme.parseHexToComposeColor()
        },
        exerciseList = exerciseListState,
        workoutPlanUiState = workoutPlanDetailState,
        onNewExerciseToWorkoutPlan = onNewExerciseToWorkoutPlan,
        onSelectExercise = onSelectExercise,
        onImageClick = { exerciseId, imageUrl ->
            onImageClick.invoke(exerciseId, imageUrl)
        },
        onDeleteExercise = {
            viewModel.deleteExercise(
                workoutPlanId = workoutPlanId,
                workoutPlanExerciseId = it
            )
        }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun WorkoutDetailView(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    workoutPlanId: Long = 0L,
    targetBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    exerciseList: List<DetailItemEntity>,
    workoutPlanUiState: WorkoutDetailUiState,
    lazyListState: LazyListState = rememberLazyListState(),
    onNewExerciseToWorkoutPlan: () -> Unit,
    onSelectExercise: (exerciseId: Long) -> Unit,
    onImageClick: (exerciseId: Long, imageUrl: String?) -> Unit,
    onDeleteExercise: (exerciseId: Long) -> Unit,
) {

    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No sharedTransitionScope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No animatedVisibilityScope found")

    var workoutPlanName by rememberSaveable {
        mutableStateOf("")
    }

    val onTopColorTheme by remember {
        derivedStateOf {
            targetBackgroundColor.bestContrastColor()
        }
    }
    var editMode by remember { mutableStateOf(false) }
    val animateAlphaValue by animateFloatAsState(
        targetValue = if (editMode) 0f else 1f,
        label = "animateAlphaValue",
    )

    val headerTitle by remember {
        derivedStateOf {
            if (editMode) "Edit" else {
                workoutPlanName
            }
        }
    }

//    BackHandler {
//        if (editMode) {
//            editMode = false
//        } else {
//            onBack.invoke()
//        }
//    }

    with (sharedTransitionScope) {
        Box(modifier = modifier) {
            Card(
                modifier = Modifier
                    .padding(
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        top = paddingValues.calculateTopPadding(),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    )
                    .sharedBounds(
                        rememberSharedContentState(
                            key = workoutPlanId
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                        boundsTransform = tabiatDetailBoundsTransform,
                    )
                    .fillMaxSize(),
                colors = CardDefaults.cardColors(
                    containerColor = targetBackgroundColor,
                    contentColor = onTopColorTheme,
                )
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    stickyHeader(contentType = "exercises") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 56.dp)
                                .background(targetBackgroundColor),
                        ) {
                            Row(modifier = Modifier.align(Alignment.CenterStart)) {
                                MainHeaderText(
                                    modifier = Modifier.padding(end = 72.dp),
                                    textTitle = headerTitle,
                                    color = onTopColorTheme
                                )
                            }

                            Row(
                                modifier = Modifier.width(72.dp).align(Alignment.CenterEnd),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                IconButton(
                                    modifier = Modifier.size(32.dp).alpha(animateAlphaValue)
                                        .scale(animateAlphaValue),
                                    enabled = !editMode,
                                    onClick = { onNewExerciseToWorkoutPlan.invoke() },
                                ) {
                                    Icon(
                                        modifier = Modifier.size(22.dp),
                                        painter = painterResource(Res.drawable.ic_plus_circle_icon_32dp),
                                        contentDescription = "",
                                        tint = onTopColorTheme
                                    )
                                }
                                EditIconButton(
                                    modifier = Modifier.size(32.dp),
                                    editMode = !editMode,
                                    onEditClick = { editMode = true },
                                    onCancelClick = { editMode = false }
                                )
                            }
                        }
                    }

                    when (workoutPlanUiState) {
                        is WorkoutDetailUiState.Error -> {}
                        WorkoutDetailUiState.Loading -> {}
                        is WorkoutDetailUiState.Success -> {
                            workoutPlanName = workoutPlanUiState.data.workoutPlan.name
                            items(
                                items = exerciseList,
                                key = { item: DetailItemEntity -> item.exerciseId },
                            ) { item: DetailItemEntity ->
                                Box(
                                    modifier = Modifier.fillMaxWidth().animateItem(),
                                ) {
                                    WorkoutExerciseItemView(
                                        modifier = Modifier.fillMaxWidth(),
                                        title = item.exerciseName,
                                        description = item.exerciseName,
                                        imageUrlList = item.exerciseImageUrlList,
                                        enabled = !editMode,
                                        onClick = {
                                            onSelectExercise(item.exerciseId)
                                        },
                                        onImageClick = {
                                            onImageClick.invoke(
                                                item.exerciseId,
                                                item.exerciseImageUrlList.first()
                                            )
                                        },
                                        progressContentView = {
                                            ExerciseFinishingStatusView(
                                                total = item.totalExerciseSet,
                                                progress = item.totalFinishedSet,
                                            )
                                        },
                                    )

                                    this@Card.AnimatedVisibility(
                                        visible = editMode,
                                        modifier = Modifier.width(48.dp).align(Alignment.CenterEnd),
                                        enter = scaleIn(
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMediumLow,
                                            ),
                                        ),
                                        exit = scaleOut(animationSpec = tween(150)),
                                    ) {
                                        Row {
                                            DeleteIconButton(
                                                onClick = {
                                                    onDeleteExercise.invoke(
                                                        item.exerciseId
                                                    )
                                                },
                                            )
                                            Spacer(Modifier.width(8.dp))
                                        }
                                    }
                                }
                            }
                        }

                        WorkoutDetailUiState.NoExercise -> {
                            editMode = false
                            item {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    EmptyState(
                                        title = "Belum ada latihan yang ditambahkan",
                                        btnText = "Tambah Latihan",
                                        onClick = {
                                            onNewExerciseToWorkoutPlan()
                                        },
                                    )
                                }
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
}
