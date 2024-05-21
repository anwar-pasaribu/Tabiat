package features.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.model.gym.GymPreferences
import features.exerciseList.BottomSheet
import org.koin.compose.koinInject
import ui.component.InsetNavigationHeight

@Composable
fun SettingBottomSheetDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    val viewModel = koinInject<SettingScreenViewModel>()
    val gymTimerOptions by viewModel.exerciseTimerOptions.collectAsState()
    val gymPreference by viewModel.gymPreferences.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadGymPreferences()
    }

    BottomSheet(
        onDismiss = { onDismiss() },
        showFullScreen = false
    ) {
        SettingPage(
            modifier = modifier,
            gymPreferences = gymPreference,
            exerciseTimerOptions = gymTimerOptions,
            onPerSetTimerClick = {
                viewModel.saveExerciseTimerDuration(it)
            },
            onPerBreakTimeClick = {
                viewModel.saveBreakTimeDuration(it)
            }
        )
    }
}

@Composable
fun SettingPage(
    modifier: Modifier = Modifier,
    gymPreferences: GymPreferences,
    exerciseTimerOptions: List<GymPreferences> = emptyList(),
    onPerSetTimerClick: (duration: Int) -> Unit = {},
    onPerBreakTimeClick: (duration: Int) -> Unit = {}
) {
    val selectedSetTimer = remember { mutableStateOf(0) }
    val selectedBreakTime = remember { mutableStateOf(0) }

    LaunchedEffect(gymPreferences) {
        selectedSetTimer.value = gymPreferences.setTimerDuration
        selectedBreakTime.value = gymPreferences.breakTimeDuration
    }

    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            text = "Durasi timer per set",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(exerciseTimerOptions.size) {
                TimerChoice(
                    modifier = Modifier
                        .weight(1F),
                    duration = exerciseTimerOptions[it].setTimerDuration,
                    label = "detik",
                    selected = selectedSetTimer.value == exerciseTimerOptions[it].setTimerDuration
                ) {
                    onPerSetTimerClick(exerciseTimerOptions[it].setTimerDuration)
                    selectedSetTimer.value = exerciseTimerOptions[it].setTimerDuration
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            text = "Durasi istirahat",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(exerciseTimerOptions.size) {
                TimerChoice(
                    modifier = Modifier
                        .weight(1F),
                    duration = exerciseTimerOptions[it].breakTimeDuration,
                    label = "menit",
                    selected = selectedBreakTime.value == exerciseTimerOptions[it].breakTimeDuration
                ) {
                    onPerBreakTimeClick(exerciseTimerOptions[it].breakTimeDuration)
                    selectedBreakTime.value = exerciseTimerOptions[it].breakTimeDuration
                }
            }
        }
        Spacer(modifier = Modifier.height(48.dp))
        InsetNavigationHeight()
    }
}

@Composable
fun TimerChoice(
    modifier: Modifier = Modifier,
    duration: Int,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val animatedColor = animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.primary.copy(alpha = .25F)
    )
    val border = if (selected) {
        BorderStroke(2.dp, animatedColor.value)
    } else {
        BorderStroke(2.dp, animatedColor.value)
    }
    OutlinedCard(
        modifier = modifier,
        onClick = { onClick() },
        border = border,
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(text = duration.toString(), style = MaterialTheme.typography.headlineLarge)
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
