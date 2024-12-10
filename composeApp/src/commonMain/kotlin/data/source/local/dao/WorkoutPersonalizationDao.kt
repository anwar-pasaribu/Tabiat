package data.source.local.dao

import com.unwur.tabiatmu.database.TabiatDatabase
import com.unwur.tabiatmu.database.WorkoutPersonalizationEntity
import domain.model.gym.WorkoutPersonalization

class WorkoutPersonalizationDao(
    private val database: TabiatDatabase
) : IWorkoutPersonalizationDao {

    override suspend fun getWorkoutPersonalizationByWorkoutPlanId(workoutPlanId: Long): WorkoutPersonalization? {
        return database
            .workoutPersonalizationQueries
            .selectWorkoutPersonalizationByWorkoutPlanId(workoutPlanId)
            .executeAsOneOrNull()?.toDomain()
    }

    override suspend fun insertWorkoutPersonalization(workoutPlanId: Long, colorTheme: String) {
        database
            .workoutPersonalizationQueries
            .insertWorkoutPersonalization(workoutPlanId = workoutPlanId, colorTheme = colorTheme)
    }

    override suspend fun updateWorkoutPersonalization(colorTheme: String, id: Long) {
        database
            .workoutPersonalizationQueries
            .updateWorkoutPersonalization(
                colorTheme = colorTheme,
                id = id
            )
    }

    override suspend fun updateWorkoutPersonalizationByWorkoutId(
        colorTheme: String,
        workoutPlanId: Long
    ) {
        database
            .workoutPersonalizationQueries
            .updateWorkoutPersonalizationByWorkoutId(
                colorTheme = colorTheme,
                workoutPlanId = workoutPlanId
            )
    }
}

private fun WorkoutPersonalizationEntity?.toDomain(): WorkoutPersonalization? {
    if (this == null) return null
    return WorkoutPersonalization(
        id = this.id,
        workoutPlanId = this.workoutPlanId,
        colorTheme = this.colorTheme
    )
}
