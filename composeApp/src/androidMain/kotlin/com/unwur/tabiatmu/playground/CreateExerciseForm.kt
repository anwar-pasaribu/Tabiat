package com.unwur.tabiatmu.playground

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import domain.model.gym.Exercise
import domain.model.gym.MuscleGroup
import features.exerciseList.BottomSheet
import ui.theme.MyAppTheme

@Composable
fun CreateExerciseForm(modifier: Modifier = Modifier, onSave: (Exercise) -> Unit) {

    var shouldShowTargetMuscleList by remember { mutableStateOf(false) }
    var exerciseMuscleTarget by remember { mutableStateOf(MuscleGroup(0, "")) }

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
                    imeAction = ImeAction.Next
                ),
                maxLines = 1
            )
            Spacer(Modifier.height(8.dp))
            ButtonRowWithAnimatedContent(
                title = "Target Otot",
                animatedText = exerciseMuscleTarget.name,
                onClick = {
                    shouldShowTargetMuscleList = true
                }
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
                    imeAction = ImeAction.Done
                ),
                maxLines = 1
            )

            Spacer(Modifier.height(32.dp))

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                enabled = exerciseName.isNotEmpty(),
                onClick = {

                }
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
            showFullScreen = false
        ) {
            val muscleTarget = listOf(
                MuscleGroup(1, "Bahu"),
                MuscleGroup(2, "Dada"),
                MuscleGroup(3, "Punggung"),
                MuscleGroup(4, "Lengan"),
                MuscleGroup(5, "Perut"),
                MuscleGroup(6, "Pantat"),
                MuscleGroup(7, "Kaki"),
            )
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Pilih Target Otot",
                style = MaterialTheme.typography.headlineSmall
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
                        }
                    ) {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = item.name, style = MaterialTheme.typography.titleLarge)
                    }
                }
                item {
                    Spacer(Modifier.windowInsetsPadding(WindowInsets.navigationBars))
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
    onClick: () -> Unit
) {
    Box(modifier = Modifier
        .clickable {
            onClick()
        }
        .fillMaxWidth()
        .height(64.dp)) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp),
        ) {
            Text(
                text = title
            )
            AnimatedVisibility(
                visible = animatedText.isNotEmpty()
            ) {
                Text(
                    text = animatedText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
            contentDescription = title
        )
    }
}

@Preview
@Composable
private fun CreateExerciseFormPrev() {
    MyAppTheme {
        CreateExerciseForm(onSave = {})
    }
}