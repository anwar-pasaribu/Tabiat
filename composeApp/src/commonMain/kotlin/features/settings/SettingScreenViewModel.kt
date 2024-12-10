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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.enums.SoundEffectType
import domain.model.gym.GymPreferences
import domain.repository.IGymRepository
import domain.usecase.GetGymPreferencesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingScreenViewModel(
    private val repository: IGymRepository,
    private val getGymPreferencesUseCase: GetGymPreferencesUseCase,
) : ViewModel() {

    private val _gymPreferences = MutableStateFlow(GymPreferences(0, 0, SoundEffectType.BOXING_BELL))
    val gymPreferences: StateFlow<GymPreferences> = _gymPreferences.asStateFlow()

    val exerciseTimerOptions: StateFlow<List<GymPreferences>> = MutableStateFlow(
        listOf(
            GymPreferences(
                setTimerDuration = 0,
                breakTimeDuration = 0,
                timerSoundEffect = SoundEffectType.NONE,
            ), GymPreferences(
                setTimerDuration = 15,
                breakTimeDuration = 5,
                timerSoundEffect = SoundEffectType.NONE,
            ),
            GymPreferences(
                setTimerDuration = 30,
                breakTimeDuration = 7,
                timerSoundEffect = SoundEffectType.BOXING_BELL,
            ),
            GymPreferences(
                setTimerDuration = 45,
                breakTimeDuration = 9,
                timerSoundEffect = SoundEffectType.UPLIFTING_BELL,
            ),
            GymPreferences(
                setTimerDuration = 60,
                breakTimeDuration = 10,
                timerSoundEffect = SoundEffectType.FAIRY_MESSAGE,
            ),
        ),
    ).asStateFlow()

    fun loadGymPreferences() {
        viewModelScope.launch {
            getGymPreferencesUseCase().collect {
                _gymPreferences.value = it.copy(
                    setTimerDuration = it.setTimerDuration,
                    breakTimeDuration = (it.breakTimeDuration / 60),
                    timerSoundEffect = it.timerSoundEffect,
                )
            }
        }
    }

    fun saveExerciseTimerDuration(durationSeconds: Int) {
        viewModelScope.launch {
            repository.saveExerciseSetTimerDuration(durationSeconds)
        }
    }

    fun saveBreakTimeDuration(durationMinutes: Int) {
        viewModelScope.launch {
            val minutesInSeconds = durationMinutes * 60
            repository.saveBreakTimeDuration(minutesInSeconds)
        }
    }

    fun saveSoundEffectSelection(soundEffectType: SoundEffectType) {
        viewModelScope.launch {
            repository.saveTimerSoundType(soundEffectType.code)
        }
    }

    fun createDummyData() {
        viewModelScope.launch {
            repository.generateDummyData()
        }
    }
}
