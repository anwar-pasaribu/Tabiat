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
package features.navigationHelper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.enums.SoundEffectType
import domain.usecase.GetGymPreferencesUseCase
import domain.usecase.GetRunningTimerPreferencesUseCase
import domain.usecase.SaveRunningTimerPreferencesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NavigationViewModel(
    getRunningTimerPreferencesUseCase: GetRunningTimerPreferencesUseCase,
    private val saveRunningTimerPreferencesUseCase: SaveRunningTimerPreferencesUseCase,
    getGymPreferencesUseCase: GetGymPreferencesUseCase,
) : ViewModel() {

    private var runningTimerCountDownStarted = false
    var currentTimerLeftDuration: StateFlow<Int> = getRunningTimerPreferencesUseCase()
        .onEach {
            startTimerAutoCountDown(it)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            initialValue = 0,
        )

    var initialTimerDuration: StateFlow<Int> = getGymPreferencesUseCase()
        .map { it.setTimerDuration }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            0,
        )

    var initialBreakTimeDuration: StateFlow<Int> = getGymPreferencesUseCase()
        .map { it.breakTimeDuration }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            0,
        )

    var timerSoundEffect: StateFlow<SoundEffectType> = getGymPreferencesUseCase()
        .map { it.timerSoundEffect }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            SoundEffectType.NONE,
        )
    val currentWorkoutPlanId = MutableStateFlow(0L)
    val currentExerciseId = MutableStateFlow(0L)

    private suspend fun startTimerAutoCountDown(timer: Int) {
        if (timer != 0 && !runningTimerCountDownStarted) {
            viewModelScope.launch {
                saveRunningTimerPreferencesUseCase.invoke(timer)
                runningTimerCountDownStarted = true
            }
        }
    }
}
