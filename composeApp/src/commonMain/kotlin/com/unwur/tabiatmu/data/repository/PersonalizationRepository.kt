package com.unwur.tabiatmu.data.repository

import com.unwur.tabiatmu.data.source.local.dao.IWorkoutPersonalizationDao
import com.unwur.tabiatmu.data.source.preferences.IPreferencesDataSource
import com.unwur.tabiatmu.domain.model.gym.WorkoutPersonalization
import com.unwur.tabiatmu.domain.repository.IPersonalizationRepository

class PersonalizationRepository(
    private val preferencesDataSource: IPreferencesDataSource,
    private val workoutPersonalizationDao: IWorkoutPersonalizationDao,
) : IPersonalizationRepository {

    override suspend fun getWorkoutPlanPersonalization(
        workoutPlanId: Long,
    ): WorkoutPersonalization {
        return workoutPersonalizationDao.getWorkoutPersonalizationByWorkoutPlanId(
            workoutPlanId = workoutPlanId
        ) ?: WorkoutPersonalization(id = 1L, workoutPlanId = workoutPlanId, colorTheme = "")
    }

    override suspend fun setWorkoutPlanPersonalization(
        workoutPlanId: Long,
        themeColorString: String
    ) {
        val currentPersonalization =
            workoutPersonalizationDao.getWorkoutPersonalizationByWorkoutPlanId(
                workoutPlanId = workoutPlanId
            )
        if (currentPersonalization != null) {
            workoutPersonalizationDao.updateWorkoutPersonalization(
                id = currentPersonalization.id,
                colorTheme = themeColorString
            )
        } else {
            workoutPersonalizationDao.insertWorkoutPersonalization(
                workoutPlanId = workoutPlanId,
                colorTheme = themeColorString
            )
        }
    }
}