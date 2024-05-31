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
    private val dataStore: DataStore<Preferences>
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
                timerSoundEffect = SoundEffectType.fromCode(soundEffectTypeCode)
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