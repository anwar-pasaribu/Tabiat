package com.unwur.tabiatmu.data.source.local.dao

import com.unwur.tabiatmu.domain.model.gym.WorkoutPersonalization

interface IWorkoutPersonalizationDao {

    suspend fun getWorkoutPersonalizationByWorkoutPlanId(workoutPlanId: Long): WorkoutPersonalization?

    suspend fun insertWorkoutPersonalization(workoutPlanId: Long, colorTheme: String)

    suspend fun updateWorkoutPersonalization(colorTheme: String, id: Long)

    suspend fun updateWorkoutPersonalizationByWorkoutId(colorTheme: String, workoutPlanId: Long)
}
