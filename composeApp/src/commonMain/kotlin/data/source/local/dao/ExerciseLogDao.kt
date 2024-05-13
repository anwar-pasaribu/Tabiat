package data.source.local.dao

import com.unwur.tabiatmu.database.TabiatDatabase
import domain.model.gym.ExerciseLog

class ExerciseLogDao(
    private val database: TabiatDatabase
) : IExerciseLogDao {
    override suspend fun getAllExerciseLog(): List<ExerciseLog> {
        return database
            .exerciseLogQueries
            .selectAllExerciseLog()
            .executeAsList()
            .map {
                ExerciseLog(
                    id = it.id,
                    exerciseId = it.exerciseId,
                    workoutPlanId = it.workoutPlanId,
                    reps = it.reps.toInt(),
                    weight = it.weight,
                    measurement = it.measurement,
                    setNumberOrder = it.setNumberOrder.toInt(),
                    finishedDateTime = it.finishedDateTime,
                    logNotes = it.logNotes.orEmpty(),
                )
            }
    }

    override suspend fun getExerciseLogByDateTimeRange(startDateTime: Long, endDateTime: Long): List<ExerciseLog> {
        return database
            .exerciseLogQueries
            .selectExerciseLogByDateTimeStampRange(startDateTime, endDateTime)
            .executeAsList()
            .map {
                ExerciseLog(
                    id = it.id,
                    exerciseId = it.exerciseId,
                    workoutPlanId = it.workoutPlanId,
                    reps = it.reps.toInt(),
                    weight = it.weight,
                    measurement = it.measurement,
                    setNumberOrder = it.setNumberOrder.toInt(),
                    finishedDateTime = it.finishedDateTime,
                    logNotes = it.logNotes.orEmpty(),
                )
            }
    }

    override suspend fun insertExerciseLog(
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
        longitude: Double
    ) {
        database
            .exerciseLogQueries
            .insertExerciseLog(
                exerciseId = exerciseId,
                workoutPlanId = workoutPlanId,
                reps = reps,
                weight = weight.toDouble(),
                difficulty = difficulty,
                measurement = measurement,
                setNumberOrder = setNumberOrder,
                finishedDateTime = finishedDateTime,
                logNotes = logNotes,
                latitude = latitude,
                longitude = longitude
            )
    }
}