package data.repository

import data.source.local.dao.IWorkoutPersonalizationDao
import data.source.preferences.IPreferencesDataSource
import domain.model.gym.WorkoutPersonalization
import domain.repository.IPersonalizationRepository

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