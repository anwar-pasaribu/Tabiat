package data.source.local.dao

import domain.model.gym.ExerciseLog

interface IExerciseLogDao {

    suspend fun getLatestExerciseLog(
        workoutPlanId: Long,
    ): ExerciseLog?

    suspend fun getAllExerciseLog(): List<ExerciseLog>

    suspend fun getExerciseLogByDateTimeRange(
        startDateTime: Long,
        endDateTime: Long,
    ): List<ExerciseLog>

    suspend fun insertExerciseLog(
        exerciseId: Long,
        workoutPlanId: Long,
        reps: Long,
        weight: Long,
        difficulty: Long,
        measurement: String,
        setNumberOrder: Long,
        finishedDateTime: Long,
        logNotes: String,
        latitude: Double,
        longitude: Double,
    )

}