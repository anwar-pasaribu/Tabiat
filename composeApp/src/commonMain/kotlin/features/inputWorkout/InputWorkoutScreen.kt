package features.inputWorkout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import ui.component.BackButton
import ui.component.MyPrimaryButton

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun InputWorkoutScreen(
    workoutPlanId: Long = 0L,
    onBack: () -> Unit = {},
    onWorkoutSaved: () -> Unit = {}
) {

    val editMode = workoutPlanId > 0
    val viewModel = koinInject<InputWorkoutScreenViewModel>()

    if (editMode) {
        viewModel.getWorkoutPlan(workoutPlanId)
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().background(Color.Transparent)
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

        Card(
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

                val workoutPlanInputTextStyle = MaterialTheme.typography.titleLarge
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    textStyle = workoutPlanInputTextStyle,
                    value = workoutName,
                    onValueChange = {
                        workoutName = it
                    },
                    supportingText = { Text("Plan workout misal: Latihan Punggung") },
                    placeholder = { Text("Nama Workout", style = workoutPlanInputTextStyle) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    maxLines = 1,
                )
                Spacer(Modifier.height(24.dp))
                HorizontalDivider(thickness = 1.dp)

                var moreDetailVisible by remember { mutableStateOf(false) }
                val rotationAnimVal by animateFloatAsState(
                    targetValue = if (moreDetailVisible) 90F else 0F
                )
                Row(
                    modifier = Modifier.clickable {
                        moreDetailVisible = !moreDetailVisible
                    }.fillMaxWidth().height(40.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "Lebih lengkap",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Icon(
                        modifier = Modifier.size(20.dp).graphicsLayer {
                            rotationZ = rotationAnimVal
                        }.alpha(.75F),
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }
                Spacer(Modifier.height(6.dp))
                AnimatedVisibility(
                    visible = moreDetailVisible
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        value = workoutNotes,
                        onValueChange = {
                            workoutNotes = it
                        },
                        supportingText = { Text("Ex. Fokus latihan pakai dumbell") },
                        placeholder = { Text("Deskripsi workout") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        ),
                        maxLines = 1
                    )
                }

                Spacer(Modifier.height(32.dp))

                MyPrimaryButton(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).align(Alignment.CenterHorizontally),
                    enabled = workoutName.isNotEmpty(),
                    textContent = {
                        if (editMode)
                            Text(text = "Ubah Workout")
                        else
                            Text(text = "Buat Workout")
                    },
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
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}