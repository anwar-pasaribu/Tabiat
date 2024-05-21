package data.source.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import domain.model.gym.GymPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : IPreferencesDataSource {

    private val exerciseSetTimerDurationKey = intPreferencesKey("exercise_set_timer_duration")
    private val breakTimeDurationKey = intPreferencesKey("exercise_break_time_duration")

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
            GymPreferences(
                setTimerDuration = it[exerciseSetTimerDurationKey] ?: 45,
                breakTimeDuration = it[breakTimeDurationKey] ?: 300
            )
        }
    }
}