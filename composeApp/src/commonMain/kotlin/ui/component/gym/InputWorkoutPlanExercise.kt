package ui.component.gym

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Delete
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
            },
            selectedExerciseId = selectedExerciseId
        )
    }

    Card(
        modifier = modifier.then(
            Modifier.fillMaxWidth()
        )
    ) {
        Column {

            Spacer(modifier = Modifier.height(16.dp))
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
                        AnimatedContent(
                            targetState = selectedExerciseName,
                            transitionSpec = {
                                (slideInVertically { height -> height } + fadeIn()).togetherWith(
                                    slideOutVertically { height -> -height } + fadeOut())
                            }
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = it,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
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

            HorizontalDivider(thickness = 1.dp)

            AnimatedVisibility(
                visible = exerciseSetList.isNotEmpty(),
                enter = scaleIn(
                    initialScale = .85F,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )),
                exit = scaleOut().plus(shrinkVertically())
            ) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    ExerciseSetListView(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        exerciseSets = exerciseSetList
                    )
                }
            }

            var addExerciseSetActionVisibility by remember { mutableStateOf(true) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {

                val lastSelectedRepetition = remember { mutableStateOf(0) }
                val lastSelectedWeight = remember { mutableStateOf(0) }
                AnimatedVisibility(visible = !addExerciseSetActionVisibility) {
                    Column {
                        Spacer(Modifier.height(16.dp))
                        AddExerciseSet(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            initialWeight = lastSelectedWeight.value,
                            initialReps = lastSelectedRepetition.value,
                            addExerciseSetDone = { reps, weight ->
                                lastSelectedRepetition.value = reps
                                lastSelectedWeight.value = weight
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
                    }
                }

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

@Composable
private fun ExerciseSetListView(
    modifier: Modifier = Modifier,
    exerciseSets: MutableList<ExerciseSet>
) {
    Card(
        modifier = modifier.then(Modifier.animateContentSize()),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        exerciseSets.forEachIndexed { index, item ->
            ExerciseSetItemView(
                modifier = Modifier,
                setNumber = index + 1,
                setCount = item.reps,
                setWeight = item.weight,
                onSetItemClick = {
                    exerciseSets.removeAt(index)
                },
                stateIcon = {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = "")
                }
            )
        }
    }
}