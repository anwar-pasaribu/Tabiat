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
package features.exerciseList

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import domain.model.gym.Exercise
import org.koin.compose.koinInject
import ui.component.BackButton
import ui.component.CategorySection
import ui.component.InsetNavigationHeight
import ui.component.gym.ExerciseListItemView
import ui.component.gym.ExerciseSearchView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    onDismiss: () -> Unit = {},
    showFullScreen: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = showFullScreen,
    )

    val noInset = BottomSheetDefaults.windowInsets
        .only(WindowInsetsSides.Bottom)
        .exclude(WindowInsets.navigationBars)

    val alphaAnimatable = remember { Animatable(0F) }

    LaunchedEffect(modalBottomSheetState.targetValue) {
        if (modalBottomSheetState.targetValue == SheetValue.PartiallyExpanded) {
            alphaAnimatable.animateTo(1F)
        } else {
            alphaAnimatable.animateTo(0F)
        }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                modifier = Modifier.alpha(alphaAnimatable.value),
            )
        },
        windowInsets = noInset,
    ) {
        content()
    }
}

@Composable
fun ExerciseListBottomSheet(
    onDismiss: () -> Unit = {},
    onSelectExercise: (Exercise) -> Unit = {},
    onCreateNewExerciseRequested: () -> Unit = {},
    selectedExerciseId: Long = 0L,
) {
    BottomSheet(
        onDismiss = onDismiss,
        showFullScreen = true,
    ) {
        ExerciseListScreen(
            modifier = Modifier,
            selectedExerciseId = selectedExerciseId,
            onBack = {
                onDismiss()
            },
            onExerciseSelected = { selectedId ->
                onSelectExercise(selectedId)
                onDismiss()
            },
            onCreateNewExerciseRequested = {
                onCreateNewExerciseRequested()
                onDismiss()
            },
        )
    }
}

@Composable
fun ExerciseListScreen(
    modifier: Modifier = Modifier,
    selectedExerciseId: Long,
    onBack: () -> Unit = {},
    onExerciseSelected: (Exercise) -> Unit = {},
    onCreateNewExerciseRequested: () -> Unit = {},
    viewModel: ExerciseListScreenViewModel = koinInject(),
) {
    val listState = rememberLazyListState()
    var scrollTopRequest by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadExerciseList()
        viewModel.loadCategoryList()
    }

    val uisState by viewModel.uiState.collectAsState()
    val exerciseList by viewModel.exerciseList.collectAsState()
    val exerciseCategoryList by viewModel.exerciseCategoryList.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(exerciseList.size, scrollTopRequest) {
        listState.scrollToItem(0)
        scrollTopRequest = false
    }

    LaunchedEffect(listState) {
        listState.interactionSource.interactions
            .collect {
                when (it) {
                    is DragInteraction.Start -> {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                }
            }
    }

    Column(modifier = modifier.then(Modifier.fillMaxSize())) {
        Box(
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            BackButton(modifier = Modifier.padding(start = 6.dp)) {
                onBack()
            }
            TextButton(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp),
                onClick = {
                    onCreateNewExerciseRequested()
                },
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
            ) {
                Text(text = "Buat Latihan Baru")
            }
        }
        ExerciseSearchView(
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp),
            onQueryChange = {
                viewModel.searchExercise(it)
            },
            onClearQuery = {
                scrollTopRequest = true
            },
        )
        Spacer(modifier = Modifier.height(8.dp))
        CategorySection(
            modifier = Modifier.fillMaxWidth(),
            exerciseCategoryList = exerciseCategoryList,
            onSelectCategory = {
                scrollTopRequest = true
                viewModel.loadExerciseByCategory(it)
            },
            onClearCategoryFilter = {
                scrollTopRequest = true
                viewModel.loadExerciseList()
            },
        )
        HorizontalDivider(thickness = 1.dp)
        when (val state = uisState) {
            ExerciseListUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator(Modifier.size(40.dp))
                }
            }

            is ExerciseListUiState.Success -> {
                ExerciseLazyList(
                    modifier = Modifier.fillMaxWidth(),
                    lazyListState = listState,
                    listExercise = state.list,
                    selectedExerciseId = selectedExerciseId,
                    onItemClick = { selectedExercise ->
                        onExerciseSelected(selectedExercise)
                    },
                )
            }

            ExerciseListUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(text = "Eh, ada masalah :(")
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.loadExerciseList()
                        },
                    ) {
                        Text(text = "Coba Lagi")
                    }
                }
            }

            is ExerciseListUiState.Filter -> {
                ExerciseLazyList(
                    modifier = Modifier.fillMaxWidth(),
                    lazyListState = listState,
                    listExercise = state.list,
                    selectedExerciseId = selectedExerciseId,
                    onItemClick = { selectedExercise ->
                        onExerciseSelected(selectedExercise)
                    },
                )
            }
            is ExerciseListUiState.Search -> {
                ExerciseLazyList(
                    modifier = Modifier.fillMaxWidth(),
                    lazyListState = listState,
                    listExercise = state.list,
                    selectedExerciseId = selectedExerciseId,
                    highlightedText = state.queryString,
                    onItemClick = { selectedExercise ->
                        onExerciseSelected(selectedExercise)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExerciseLazyList(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    listExercise: List<Exercise>,
    selectedExerciseId: Long = 0L,
    highlightedText: String = "",
    onItemClick: (Exercise) -> Unit,
) {
    AnimatedContent(
        targetState = listExercise.isEmpty(),
        transitionSpec = {
            (fadeIn(animationSpec = tween(220, delayMillis = 90)))
                .togetherWith(fadeOut(animationSpec = tween(90)))
        },
    ) { isEmpty ->
        if (isEmpty) {
            Column(
                modifier = modifier.then(Modifier.height(300.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.List,
                    contentDescription = "",
                )
                Spacer(Modifier.height(16.dp))
                Text(text = "Belum ada data latihan")
            }
        } else {
            LazyColumn(
                modifier = modifier,
                state = lazyListState,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            ) {
                items(items = listExercise, key = { it.id }) { exercise ->
                    ExerciseListItemView(
                        modifier = Modifier.animateItemPlacement(
                            animationSpec = tween(delayMillis = 150),
                        ).padding(bottom = 8.dp),
                        selected = exercise.id == selectedExerciseId,
                        highlightedText = highlightedText,
                        title = exercise.name,
                        description = exercise.description,
                        imageUrlList = exercise.imageList,
                        image = exercise.image,
                        onClick = {
                            onItemClick(exercise)
                        },
                    )
                }
                item {
                    InsetNavigationHeight()
                }
            }
        }
    }
}
