package features.inputWorkout

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class
)
@Composable
fun InputWorkoutScreen(
    onBack: () -> Unit = {},
    onWorkoutSaved: () -> Unit = {}
) {

    val hazeState = remember { HazeState() }
    val selectedEmojiUnicodes = remember { mutableStateListOf("") }
    var selectedEmojiUnicode by remember { mutableStateOf("") }
    var selectedEmojiOffset by remember { mutableStateOf(Offset.Zero) }
    val lazyListState = rememberLazyListState()

    val viewModel = koinInject<InputWorkoutScreenViewModel>()

    val selectedEmojiUnicodeAndOffset =
        remember { mutableStateOf(MutableStateFlow(Pair("", Offset.Zero))) }

    var selectedDateTimeStamp by remember { mutableStateOf(0L) }
    var moodStateBottomSheetStateShowed by remember { mutableStateOf(false) }

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
                            "New Workout",
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
                var workoutName by remember { mutableStateOf("") }
                var workoutNotes by remember { mutableStateOf("") }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    value = workoutName,
                    onValueChange = {
                        workoutName = it
                    },
                    label = { Text("Workout Name") },
                    placeholder = { Text("Ex. Back session") },
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
                    label = { Text("Notes") },
                    placeholder = { Text("Ex. Focus on weight") },
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
                        viewModel.saveWorkout(workoutName, workoutNotes)
                        onWorkoutSaved()
                    }
                ) {
                    Text( text = "Save Workout")
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}