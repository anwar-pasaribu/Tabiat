package domain.repository

import domain.model.gym.Exercise
import domain.model.gym.ExerciseLog
import domain.model.gym.ExerciseSet
import domain.model.gym.WorkoutPlan
import domain.model.gym.WorkoutPlanExercise

interface IGymRepository {
    suspend fun createWorkoutPlan(workoutName: String, notes: String): Boolean
    suspend fun createNewExercise(newExercise: Exercise): Boolean

    suspend fun getWorkoutPlans(): List<WorkoutPlan>
    suspend fun getExercises(): List<Exercise>
    suspend fun getWorkoutPlanExercises(workoutPlanId: Long): List<Exercise>

    suspend fun getExerciseSetList(workoutPlanId: Long, exerciseId: Long): List<WorkoutPlanExercise>
    suspend fun insertExerciseSetList(
        workoutPlanId: Long,
        exerciseId: Long,
        exerciseSetList: List<ExerciseSet>
    ): Boolean

    suspend fun getWorkoutPlanById(workoutPlanId: Long): WorkoutPlan

    suspend fun getExerciseById(exerciseId: Long): Exercise
    suspend fun logExercise(
        workoutPlanExerciseId: Long,
        workoutPlanId: Long,
        exerciseId: Long,
        reps: Int,
        weight: Int
    ): Boolean

    suspend fun getExerciseLogListByDateTimeStamp(dateTimeStamp: Long): List<ExerciseLog>
}