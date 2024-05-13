package data.source.local.dao

import domain.model.gym.WorkoutPlanExercise
import kotlinx.coroutines.flow.Flow

interface IWorkoutPlanExerciseDao {
    suspend fun getAllWorkoutPlanExerciseObservable(workoutPlanId: Long): Flow<List<WorkoutPlanExercise>>
    suspend fun getAllWorkoutPlanExercise(workoutPlanId: Long): List<WorkoutPlanExercise>
    suspend fun getLatestWorkoutPlanExercise(): WorkoutPlanExercise
    suspend fun insertWorkoutPlanExercise(
        exerciseId: Long,
        workoutPlanId: Long,
        reps: Long,
        weight: Long,
        setNumberOrder: Long,
        finishedDateTime: Long,
    )

    suspend fun updateWorkoutPlanExerciseFinishedDateTime(
        workoutPlanExerciseId: Long,
        finishedDateTime: Long,
        reps: Long,
        weight: Long
    )
}