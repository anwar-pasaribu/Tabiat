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
package features.createNewExercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import domain.model.gym.Exercise
import features.exerciseList.BottomSheet
import org.koin.compose.koinInject
import ui.component.InsetNavigationHeight

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalHazeMaterialsApi::class,
)
@Composable
fun CreateExerciseScreen(
    contentPadding: PaddingValues,
    onBack: () -> Unit = {},
    onNewExerciseCreated: () -> Unit = {},
) {
    val hazeState = remember { HazeState() }

    val viewModel = koinInject<CreateExerciseScreenViewModel>()

    val muscleGroups by viewModel.exerciseMuscleTargetList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMuscleGroupList()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Card(
            modifier = Modifier.padding(
                start = contentPadding.calculateStartPadding(LayoutDirection.Ltr) + 8.dp,
                top = contentPadding.calculateTopPadding() + 16.dp,
                end = contentPadding.calculateEndPadding(LayoutDirection.Ltr) + 8.dp,
                bottom = contentPadding.calculateBottomPadding(),
            ),
        ) {
            CreateExerciseForm(
                modifier = Modifier.fillMaxWidth(),
                muscleGroups = muscleGroups,
                onSave = {
                    viewModel.saveExercise(it)
                    onNewExerciseCreated()
                },
            )
        }
    }
}

@Composable
fun CreateExerciseForm(
    modifier: Modifier = Modifier,
    onSave: (Exercise) -> Unit,
    muscleGroups: List<String> = emptyList(),
) {
    var shouldShowTargetMuscleList by remember { mutableStateOf(false) }
    var exerciseMuscleTarget by remember { mutableStateOf("") }

    Surface {
        Column(modifier = modifier.then(Modifier)) {
            var exerciseName by remember { mutableStateOf("") }
            var exerciseNotes by remember { mutableStateOf("") }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = exerciseName,
                onValueChange = {
                    exerciseName = it
                },
                label = { Text("Nama Latihan") },
                placeholder = { Text("Ex. Dumbell Curl") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                ),
                maxLines = 1,
            )
            Spacer(Modifier.height(8.dp))
            ButtonRowWithAnimatedContent(
                title = "Target Otot",
                animatedText = exerciseMuscleTarget,
                onClick = {
                    shouldShowTargetMuscleList = true
                },
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = exerciseNotes,
                onValueChange = {
                    exerciseNotes = it
                },
                label = { Text("Deskripsi") },
                placeholder = { Text("Ex. Angkat Dumbell") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                maxLines = 1,
            )

            Spacer(Modifier.height(32.dp))

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                enabled = exerciseName.isNotEmpty(),
                onClick = {
                    onSave(
                        Exercise(
                            id = 0L,
                            name = exerciseName,
                            description = exerciseNotes,
                            targetMuscle = exerciseMuscleTarget,
                            difficulty = 0,
                            equipment = "",
                            instructions = "",
                            video = "",
                            image = "",
                        ),
                    )
                },
            ) {
                Text(text = "Tambah Latihan")
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    if (shouldShowTargetMuscleList) {
        BottomSheet(
            onDismiss = {
                shouldShowTargetMuscleList = false
            },
            showFullScreen = false,
        ) {
            val muscleTarget = muscleGroups
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Pilih Target Otot",
                style = MaterialTheme.typography.headlineSmall,
            )
            LazyColumn {
                items(items = muscleTarget) { item ->
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .fillMaxWidth(),
                        onClick = {
                            exerciseMuscleTarget = item
                            shouldShowTargetMuscleList = false
                        },
                    ) {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = item,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
                item {
                    InsetNavigationHeight()
                }
            }
        }
    }
}

@Composable
fun ButtonRowWithAnimatedContent(
    modifier: Modifier = Modifier,
    title: String,
    animatedText: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth()
            .height(64.dp),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp),
        ) {
            Text(
                text = title,
            )
            AnimatedVisibility(
                visible = animatedText.isNotEmpty(),
            ) {
                Text(
                    text = animatedText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }

        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
            contentDescription = title,
        )
    }
}
