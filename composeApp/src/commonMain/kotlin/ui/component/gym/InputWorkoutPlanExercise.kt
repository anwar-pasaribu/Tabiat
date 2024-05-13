package ui.component.gym

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import domain.model.gym.ExerciseSet
import features.exerciseList.ExerciseListBottomSheet

@Composable
fun InputWorkoutPlanExerciseView(
    modifier: Modifier = Modifier,
    onSave: (exerciseId: Long, exerciseSetList: List<ExerciseSet>) -> Unit,
    onCreateNewExerciseRequested: () -> Unit = {}
) {
    var shouldShowExerciseList by remember { mutableStateOf(false) }
    var selectedExerciseName by remember { mutableStateOf("") }
    var selectedExerciseId by remember { mutableStateOf(0L) }
    val exerciseSetList = remember { mutableStateListOf<ExerciseSet>() }

    if (shouldShowExerciseList) {
        ExerciseListBottomSheet(
            onDismiss = {
                shouldShowExerciseList = false
            },
            onSelectExercise = {
                selectedExerciseId = it.id
                selectedExerciseName = it.name
            },
            onCreateNewExerciseRequested = {
                onCreateNewExerciseRequested()
            }
        )
    }

    Card(
        modifier = modifier.then(
            Modifier.fillMaxWidth()
        )
    ) {
        Column {

            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier
                .clickable {
                    shouldShowExerciseList = true
                }
                .fillMaxWidth()
                .height(64.dp)) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp),
                ) {
                    Text(
                        text = "Pilih latihan"
                    )
                    AnimatedVisibility(
                        visible = selectedExerciseName.isNotEmpty()
                    ) {
                        Text(
                            text = selectedExerciseName,
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
                    contentDescription = "Pilih latihan"
                )
            }

            HorizontalDivider(thickness = 10.dp)

            var addExerciseSetActionVisibility by remember { mutableStateOf(true) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {

                Box(modifier = Modifier
                    .clickable {
                        addExerciseSetActionVisibility = !addExerciseSetActionVisibility
                    }
                    .fillMaxWidth()
                    .height(56.dp)) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp),
                        text = "Tambah Set"
                    )

                    Icon(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp),
                        imageVector = if (addExerciseSetActionVisibility) Icons.Default.Add else Icons.Default.Clear,
                        contentDescription = ""
                    )
                }


                AnimatedVisibility(visible = !addExerciseSetActionVisibility) {
                    Column {
                        AddExerciseSet(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            addExerciseSetDone = { reps, weight ->
                                exerciseSetList.add(
                                    ExerciseSet(
                                        setNumber = exerciseSetList.size + 1,
                                        reps = reps,
                                        weight = weight
                                    )
                                )
                                addExerciseSetActionVisibility = true
                            }
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            AnimatedVisibility(visible = exerciseSetList.isNotEmpty()) {
                ExerciseSetListView(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    exerciseSets = exerciseSetList
                )
            }

            Spacer(Modifier.height(32.dp))

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                enabled = exerciseSetList.isNotEmpty() && selectedExerciseId != 0L,
                onClick = {
                    onSave(selectedExerciseId, exerciseSetList)
                }
            ) {
                Text(text = "Simpan Latihan")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ExerciseSetListView(
    modifier: Modifier = Modifier,
    exerciseSets: List<ExerciseSet>
) {

    Card(
        modifier = modifier.then(Modifier),
        onClick = { /*TODO*/ },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        LazyColumn(reverseLayout = false) {
            items(items = exerciseSets, key = { it.setNumber }) { item ->
                ExerciseSetItemView(
                    modifier = Modifier.animateItemPlacement(),
                    setNumber = item.setNumber,
                    setCount = item.reps,
                    setWeight = item.weight
                ) {

                }
            }
        }
    }
}