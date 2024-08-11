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
        timerSoundEffect = SoundEffectType.BOXING_BELL,
    ),
    GymPreferences(
        setTimerDuration = 30,
        breakTimeDuration = 7,
        timerSoundEffect = SoundEffectType.BOXING_BELL,
    ),
    GymPreferences(
        setTimerDuration = 45,
        breakTimeDuration = 9,
        timerSoundEffect = SoundEffectType.BOXING_BELL,
    ),
    GymPreferences(
        setTimerDuration = 60,
        breakTimeDuration = 10,
        timerSoundEffect = SoundEffectType.BOXING_BELL,
    ),
)

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun WorkoutListItemViewPreview() {
    MyAppTheme {
        Column {
            SettingPage(
                gymPreferences = GymPreferences(45, 5, SoundEffectType.BOXING_BELL),
                exerciseTimerOptions = gymOptionList,
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
