package data.source.local.dao

import domain.model.gym.WorkoutPlan
import kotlinx.coroutines.flow.Flow

interface IWorkoutPlanDao {
    suspend fun getAllWorkoutPlanObservable(): Flow<List<WorkoutPlan>>
    suspend fun getAllWorkoutPlan(): List<WorkoutPlan>
    suspend fun getLatestWorkoutPlan(): WorkoutPlan
    suspend fun getWorkoutPlan(workoutPlanId: Long): WorkoutPlan
    suspend fun insertWorkoutPlan(
        name: String,
        description: String,
        datetimeStamp: Long,
        orderingNumber: Int
    )
    suspend fun updateWorkoutPlan(
        workoutPlanId: Long,
        name: String,
        description: String,
    )

    suspend fun deleteWorkoutPlan(
        workoutPlanId: Long
    )
}