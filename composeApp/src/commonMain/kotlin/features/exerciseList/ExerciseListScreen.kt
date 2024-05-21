package features.exerciseList

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import domain.model.gym.Exercise
import org.koin.compose.koinInject
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
        skipPartiallyExpanded = showFullScreen
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
                modifier = Modifier.alpha(alphaAnimatable.value)
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
        showFullScreen = true
    ) {
        ExerciseListScreen(
            modifier = Modifier,
            selectedExerciseId = selectedExerciseId,
            onExerciseSelected = { selectedId ->
                onSelectExercise(selectedId)
                onDismiss()
            },
            onCreateNewExerciseRequested = {
                onCreateNewExerciseRequested()
                onDismiss()
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExerciseListScreen(
    modifier: Modifier = Modifier,
    selectedExerciseId: Long,
    onExerciseSelected: (Exercise) -> Unit = {},
    onCreateNewExerciseRequested: () -> Unit = {},
    viewModel: ExerciseListScreenViewModel = koinInject(),
) {

    val listState = rememberLazyListState()
    var scrollTopRequest by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadExerciseList()
    }

    LaunchedEffect(scrollTopRequest) {
        if (scrollTopRequest) {
            listState.animateScrollToItem(0)
            scrollTopRequest = false
        }
    }

    val exerciseList by viewModel.exerciseList.collectAsState()

    Column(modifier = modifier.then(Modifier.fillMaxSize())) {

        Box(
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            TextButton(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp),
                onClick = {
                    onCreateNewExerciseRequested()
                },
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
            ) {
                Text(text = "Buat Latihan Baru")
            }
        }
        ExerciseSearchView(
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp),
            onQueryChange = {
                viewModel.searchExercise(it)
                scrollTopRequest = true
            },
            onClearQuery = {
                scrollTopRequest = true
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 1.dp)
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
        ) {
            items(items = exerciseList, key = { it.id }) { exercise ->
                ExerciseListItemView(
                    modifier = Modifier.animateItemPlacement().padding(bottom = 8.dp),
                    selected = exercise.id == selectedExerciseId,
                    title = exercise.name,
                    description = exercise.description,
                    imageUrlList = exercise.imageList,
                    image = exercise.image,
                    onClick = {
                        onExerciseSelected(exercise)
                    },
                )
            }
            item {
                InsetNavigationHeight()
            }
        }

    }
}