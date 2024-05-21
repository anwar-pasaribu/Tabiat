package data.source.preferences

import domain.model.gym.GymPreferences
import kotlinx.coroutines.flow.Flow

interface IPreferencesDataSource {
    suspend fun saveExerciseSetTimerDuration(durationSeconds: Int)
    suspend fun saveExerciseBreakTimeDuration(durationSeconds: Int)
    fun getGymPreference(): Flow<GymPreferences>
}