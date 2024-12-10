package data.source.local.dao

import domain.model.gym.WorkoutPersonalization

interface IWorkoutPersonalizationDao {

    suspend fun getWorkoutPersonalizationByWorkoutPlanId(workoutPlanId: Long): WorkoutPersonalization?

    suspend fun insertWorkoutPersonalization(workoutPlanId: Long, colorTheme: String)

    suspend fun updateWorkoutPersonalization(colorTheme: String, id: Long)

    suspend fun updateWorkoutPersonalizationByWorkoutId(colorTheme: String, workoutPlanId: Long)
}
