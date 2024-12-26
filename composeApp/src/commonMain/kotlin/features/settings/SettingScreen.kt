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
package features.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import domain.enums.SoundEffectType
import domain.model.gym.GymPreferences
import features.exerciseList.BottomSheet
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import platform.PlaySoundEffect
import tabiat.composeapp.generated.resources.Res
import tabiat.composeapp.generated.resources.ic_pause_icon_32dp
import tabiat.composeapp.generated.resources.ic_timer_off_icon_32dp
import ui.component.ImageWrapper
import ui.component.InsetNavigationHeight
import ui.component.MainHeaderText

@Composable
fun SettingBottomSheetDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    val viewModel = koinInject<SettingScreenViewModel>()
    val gymTimerOptions by viewModel.exerciseTimerOptions.collectAsState()
    val gymPreference by viewModel.gymPreferences.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadGymPreferences()
    }

    BottomSheet(
        onDismiss = { onDismiss() },
        showFullScreen = false,
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
            },
            onTimerSoundChangeClick = {
                viewModel.saveSoundEffectSelection(it)
            },
            onCreateDummyData = {
                viewModel.createDummyData()
            },
        )
    }
}

@Composable
fun SettingsScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinInject<SettingScreenViewModel>()
    val gymTimerOptions by viewModel.exerciseTimerOptions.collectAsState()
    val gymPreference by viewModel.gymPreferences.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadGymPreferences()
    }

    val settingContentPadding = PaddingValues(
        start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
        top = contentPadding.calculateTopPadding(),
        end = contentPadding.calculateEndPadding(LayoutDirection.Ltr)
    )

    Surface(
        color = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(settingContentPadding)
                .verticalScroll(
                    state = rememberScrollState()
                )
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
                },
                onTimerSoundChangeClick = {
                    viewModel.saveSoundEffectSelection(it)
                },
                onCreateDummyData = {
                    viewModel.createDummyData()
                },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingPage(
    modifier: Modifier = Modifier,
    gymPreferences: GymPreferences,
    exerciseTimerOptions: List<GymPreferences> = emptyList(),
    onPerSetTimerClick: (duration: Int) -> Unit = {},
    onPerBreakTimeClick: (duration: Int) -> Unit = {},
    onTimerSoundChangeClick: (soundEffectType: SoundEffectType) -> Unit = {},
    onCreateDummyData: () -> Unit = {},
) {
    val selectedSetTimer = remember { mutableStateOf(0) }
    val selectedBreakTime = remember { mutableStateOf(0) }
    var selectedTimerSound by remember { mutableStateOf(gymPreferences.timerSoundEffect) }

    LaunchedEffect(gymPreferences) {
        selectedSetTimer.value = gymPreferences.setTimerDuration
        selectedBreakTime.value = gymPreferences.breakTimeDuration
        selectedTimerSound = gymPreferences.timerSoundEffect
    }

    Column(modifier = modifier) {
        MainHeaderText(
            modifier = Modifier
                .combinedClickable(
                    enabled = true,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                    onDoubleClick = {
                        onCreateDummyData()
                    },
                )
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            textTitle = "Settings"
        )
        SettingSectionLabel(title = "Durasi Timer Per Set")
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
                    selected = selectedSetTimer.value == exerciseTimerOptions[it].setTimerDuration,
                ) {
                    onPerSetTimerClick(exerciseTimerOptions[it].setTimerDuration)
                    selectedSetTimer.value = exerciseTimerOptions[it].setTimerDuration
                }
            }
        }
        SettingSectionLabel(title = "Durasi Istirahat")
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
                    selected = selectedBreakTime.value == exerciseTimerOptions[it].breakTimeDuration,
                ) {
                    onPerBreakTimeClick(exerciseTimerOptions[it].breakTimeDuration)
                    selectedBreakTime.value = exerciseTimerOptions[it].breakTimeDuration
                }
            }
        }
        SettingSectionLabel(title = "Efek Suara Timer")
        TimerSoundSection(
            modifier = Modifier.fillMaxWidth(),
            selectedTimerSound = selectedTimerSound,
            onSoundEffectSelected = {
                selectedTimerSound = it
                onTimerSoundChangeClick(it)
            },
        )
        AboutAppSection()
        Spacer(modifier = Modifier.height(48.dp))
        InsetNavigationHeight()
    }
}

@Composable
private fun ColumnScope.SettingSectionLabel(modifier: Modifier = Modifier, title: String) {
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        text = title,
        style = MaterialTheme.typography.titleMedium,
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun ColumnScope.SettingListMenu(
    modifier: Modifier = Modifier,
    menuIcon: ImageVector,
    textContent: @Composable RowScope.() -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.clickable { onClick() }
            .then(modifier),
    ) {
        Icon(
            imageVector = menuIcon,
            contentDescription = null,
        )
        textContent()
    }
}

@Composable
fun TimerChoice(
    modifier: Modifier = Modifier,
    duration: Int,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val animatedColor = animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.primary.copy(alpha = .25F)
        },
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
        Column(Modifier.fillMaxWidth().padding(8.dp)) {
            if (duration == 0) {
                ImageWrapper(
                    modifier = Modifier.padding(top = 6.dp, bottom = 2.dp).size(32.dp),
                    resource = Res.drawable.ic_timer_off_icon_32dp,
                    colorFilter = ColorFilter.tint(
                        MaterialTheme.colorScheme.onSurface
                    ),
                    contentDescription = "No Timer"
                )
                Text(
                    modifier = Modifier,
                    text = "off", style = MaterialTheme.typography.bodyLarge)
            } else {
                Text(
                    modifier = Modifier.align(Alignment.Start),
                    text = duration.toString(), style = MaterialTheme.typography.headlineLarge)
                Text(text = label, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun TimerSoundSection(
    modifier: Modifier = Modifier,
    selectedTimerSound: SoundEffectType,
    onSoundEffectSelected: (SoundEffectType) -> Unit,
) {
    var selectedSoundEffectType by remember {
        mutableStateOf(selectedTimerSound)
    }

    var playSoundEffectByType by remember {
        mutableStateOf(SoundEffectType.NONE)
    }
    LaunchedEffect(selectedTimerSound) {
        selectedSoundEffectType = selectedTimerSound
    }

    if (playSoundEffectByType != SoundEffectType.NONE) {
        PlaySoundEffect(
            playSoundEffectByType,
            playSoundEffectByType,
        )
    }
    val soundTypes = SoundEffectType.entries.toTypedArray()
    Row(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        soundTypes.forEach {
            SoundChoice(
                soundEffectType = it,
                selected = it == selectedSoundEffectType,
            ) {
                playSoundEffectByType = it
                selectedSoundEffectType = it
                onSoundEffectSelected(it)
            }
        }
    }
}

@Composable
fun SoundChoice(
    modifier: Modifier = Modifier,
    soundEffectType: SoundEffectType,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val animatedColor = animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.primary.copy(alpha = .25F)
        },
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
            if (soundEffectType == SoundEffectType.NONE) {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(Res.drawable.ic_pause_icon_32dp),
                    contentDescription = null
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.PlayArrow,
                    contentDescription = null
                )
            }
            Text(
                text = soundEffectType.name.lowercase().replace("_", " "),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun AboutAppSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "© 2024 Tabiat (by Anwar Pasaribu)\n" +
                    "All rights reserved.",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}
