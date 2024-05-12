package features.exerciseList

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import domain.model.gym.Exercise
import org.koin.compose.koinInject
import ui.component.gym.ExerciseListItemView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListBottomSheet(
    onDismiss: () -> Unit = {},
    onSelectExercise: (Exercise) -> Unit = {},
    showFullScreen: Boolean = true,
    selectedExerciseId: Long = 0L,
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
        ExerciseListScreen(
            modifier = Modifier,
            selectedExerciseId = selectedExerciseId,
            onExerciseSelected = { selectedId ->
                onSelectExercise(selectedId)
                onDismiss()
            }
        )
    }
}

@Composable
fun ExerciseListScreen(
    modifier: Modifier = Modifier,
    selectedExerciseId: Long,
    onExerciseSelected: (Exercise) -> Unit = {},
    viewModel: ExerciseListScreenViewModel = koinInject(),
) {

    LaunchedEffect(Unit) {
        viewModel.loadExerciseList()
    }

    val exerciseList by viewModel.exerciseList.collectAsState()

    Column(modifier = modifier.then(Modifier.fillMaxSize())) {

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(items = exerciseList, key = { it.id }) { exercise ->
                ExerciseListItemView(
                    title = exercise.name,
                    description = exercise.description,
                    onClick = {
                        onExerciseSelected(exercise)
                    },
                )
            }
        }

    }
}