package com.unwur.tabiatmu.playground

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import domain.enums.SoundEffectType
import domain.model.gym.GymPreferences
import features.settings.AboutAppSection
import features.settings.SettingPage
import features.settings.TimerSoundSection
import ui.theme.MyAppTheme

private val gymOptionList = listOf(
    GymPreferences(
        setTimerDuration = 15,
        breakTimeDuration = 5,
        timerSoundEffect = SoundEffectType.BOXING_BELL
    ),
    GymPreferences(
        setTimerDuration = 30,
        breakTimeDuration = 7,
        timerSoundEffect = SoundEffectType.BOXING_BELL
    ),
    GymPreferences(
        setTimerDuration = 45,
        breakTimeDuration = 9,
        timerSoundEffect = SoundEffectType.BOXING_BELL
    ),
    GymPreferences(
        setTimerDuration = 60,
        breakTimeDuration = 10,
        timerSoundEffect = SoundEffectType.BOXING_BELL
    ),
)

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun WorkoutListItemViewPreview() {
    MyAppTheme {
        Column {
            SettingPage(
                gymPreferences = GymPreferences(45, 5, SoundEffectType.BOXING_BELL),
                exerciseTimerOptions = gymOptionList
            )
        }
    }
}

@Preview
@Composable
private fun TimerSoundSectionPrev() {
    MyAppTheme {
        Column {
            TimerSoundSection(selectedTimerSound = SoundEffectType.BOXING_BELL) {

            }

            AboutAppSection()
        }
    }
}

