package features.inputWorkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import org.koin.compose.koinInject
import ui.component.BackButton

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class
)
@Composable
fun InputWorkoutScreen(
    workoutPlanId: Long = 0L,
    onBack: () -> Unit = {},
    onWorkoutSaved: () -> Unit = {}
) {

    val editMode = workoutPlanId > 0
    val hazeState = remember { HazeState() }

    val viewModel = koinInject<InputWorkoutScreenViewModel>()

    if (editMode) {
        viewModel.getWorkoutPlan(workoutPlanId)
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
                        BackButton(
                            onClick = { onBack() },
                        )
                    },
                    title = {
                        Text(
                            if (editMode) "Ubah Workout" else "Workout Baru",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
        },
    ) { contentPadding ->

        Card (
            modifier = Modifier.fillMaxWidth().padding(
                start = contentPadding.calculateStartPadding(LayoutDirection.Ltr) + 8.dp,
                top = contentPadding.calculateTopPadding() + 16.dp,
                end = contentPadding.calculateEndPadding(LayoutDirection.Ltr) + 8.dp,
                bottom = contentPadding.calculateBottomPadding()
            )
        ) {
            Column {

                val workoutPlan by viewModel.workoutPlan.collectAsState()
                var workoutName by remember { mutableStateOf(workoutPlan?.name.orEmpty()) }
                var workoutNotes by remember { mutableStateOf(workoutPlan?.description.orEmpty()) }

                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    value = workoutName,
                    onValueChange = {
                        workoutName = it
                    },
                    label = { Text("Nama Sesi Workout") },
                    placeholder = { Text("Ex. Latihan Punggung") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    maxLines = 1
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    value = workoutNotes,
                    onValueChange = {
                        workoutNotes = it
                    },
                    label = { Text("Deskripsi") },
                    placeholder = { Text("Ex. Fokus latihan pakai dumbell") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    maxLines = 1
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    modifier = Modifier.width(200.dp).align(Alignment.CenterHorizontally),
                    enabled = workoutName.isNotEmpty(),
                    onClick = {
                        if (editMode) {
                            viewModel.saveWorkout(
                                workoutPlanId,
                                workoutName,
                                workoutNotes
                            )
                        } else {
                            viewModel.createNewWorkoutPlan(workoutName, workoutNotes)
                        }
                        onWorkoutSaved()
                    }
                ) {
                    if (editMode)
                        Text(text = "Ubah Workout")
                    else
                        Text(text = "Buat Workout")
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}