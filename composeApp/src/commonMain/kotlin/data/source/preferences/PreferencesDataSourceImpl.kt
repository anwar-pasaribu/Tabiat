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
package data.source.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import domain.enums.SoundEffectType
import domain.model.gym.GymPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PreferencesDataSourceImpl(
    private val dataStore: DataStore<Preferences>,
) : IPreferencesDataSource {

    private val exerciseSetTimerDurationKey = intPreferencesKey("exercise_set_timer_duration")
    private val breakTimeDurationKey = intPreferencesKey("exercise_break_time_duration")
    private val timerSoundTypeKey = intPreferencesKey("timer_sound_type_key")
    private val lastResetExerciseFinishedDate = longPreferencesKey("last_reset_exercise_finished_date")
    private val runningTimerDurationInSecond = intPreferencesKey("running_timer_duration_in_second")

    override suspend fun saveExerciseSetTimerDuration(durationSeconds: Int) {
        dataStore.edit {
            it[exerciseSetTimerDurationKey] = durationSeconds
        }
    }

    override suspend fun saveExerciseBreakTimeDuration(durationSeconds: Int) {
        dataStore.edit {
            it[breakTimeDurationKey] = durationSeconds
        }
    }

    override fun getGymPreference(): Flow<GymPreferences> {
        return dataStore.data.map {
            val soundEffectTypeCode = it[timerSoundTypeKey] ?: SoundEffectType.BOXING_BELL.code
            GymPreferences(
                setTimerDuration = it[exerciseSetTimerDurationKey] ?: 45,
                breakTimeDuration = it[breakTimeDurationKey] ?: 300,
                timerSoundEffect = SoundEffectType.fromCode(soundEffectTypeCode),
            )
        }
    }

    override suspend fun setTimerSoundType(timerSoundType: Int) {
        dataStore.edit {
            it[timerSoundTypeKey] = timerSoundType
        }
    }

    override suspend fun saveLastExerciseReset(lastResetTimeStamp: Long) {
        dataStore.edit {
            it[lastResetExerciseFinishedDate] = lastResetTimeStamp
        }
    }

    override suspend fun getLastExerciseResetTimeStamp(): Long {
        val preferences = dataStore.data.first()
        return preferences[lastResetExerciseFinishedDate] ?: 0L
    }

    override suspend fun saveRunningTimerDuration(seconds: Int) {
        dataStore.edit {
            it[runningTimerDurationInSecond] = seconds
        }
    }

    override fun getRunningTimerDurationTimeStamp(): Flow<Int> {
        return dataStore.data.map {
            it[runningTimerDurationInSecond] ?: 0
        }
    }
}
