package com.unwur.tabiatmu.domain.repository

import com.unwur.tabiatmu.domain.model.gym.WorkoutPersonalization

interface IPersonalizationRepository {
    suspend fun getWorkoutPlanPersonalization(workoutPlanId: Long): WorkoutPersonalization
    suspend fun setWorkoutPlanPersonalization(
        workoutPlanId: Long, themeColorString: String,
    )
}