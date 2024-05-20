package features.inputExercise

import PlayHapticAndSound
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import org.koin.compose.koinInject
import ui.component.InsetNavigationHeight
import ui.component.gym.InputWorkoutPlanExerciseView

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class
)
@Composable
fun InputExerciseScreen(
    workoutPlanId: Long,
    onBack: () -> Unit = {},
    onCreateNewExerciseRequested: () -> Unit = {},
) {

    val hazeState = remember { HazeState() }
    var selectedEmojiUnicode by remember { mutableStateOf("") }


    val viewModel = koinInject<InputExerciseScreenViewModel>()


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
                            "Tambah Sesi Latihan",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
        },
    ) { contentPadding ->

        LazyColumn(
            modifier = Modifier.fillMaxSize().haze(hazeState),
            contentPadding = PaddingValues(
                start = contentPadding.calculateStartPadding(LayoutDirection.Ltr) + 8.dp,
                top = contentPadding.calculateTopPadding() + 16.dp,
                end = contentPadding.calculateEndPadding(LayoutDirection.Ltr) + 8.dp,
                bottom = contentPadding.calculateBottomPadding()
            )
        ) {
            item {
                InputWorkoutPlanExerciseView (
                    modifier = Modifier,
                    onSave = { exerciseId, exerciseSetList ->
                        viewModel.insertExerciseSetList(workoutPlanId, exerciseId, exerciseSetList)
                        onBack()
                    },
                    onCreateNewExerciseRequested = {
                        onCreateNewExerciseRequested()
                    }
                )
            }
            item {
                InsetNavigationHeight()
            }
        }

    }
}