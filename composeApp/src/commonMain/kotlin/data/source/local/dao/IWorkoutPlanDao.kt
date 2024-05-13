package data.source.local.dao

import domain.model.gym.WorkoutPlan
import kotlinx.coroutines.flow.Flow

interface IWorkoutPlanDao {
    suspend fun getAllWorkoutPlanObservable(): Flow<List<WorkoutPlan>>
    suspend fun getAllWorkoutPlan(): List<WorkoutPlan>
    suspend fun getLatestWorkoutPlan(): WorkoutPlan
    suspend fun insertWorkoutPlan(
        name: String,
        description: String,
        datetimeStamp: Long,
        orderingNumber: Int
    )
}