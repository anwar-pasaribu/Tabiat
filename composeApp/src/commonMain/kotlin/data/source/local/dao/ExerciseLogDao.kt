package data.source.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.unwur.tabiatmu.database.ExerciseLogEntity
import com.unwur.tabiatmu.database.TabiatDatabase
import domain.model.gym.ExerciseLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExerciseLogDao(
    private val database: TabiatDatabase
) : IExerciseLogDao {

    override suspend fun getLatestExerciseLog(workoutPlanId: Long): ExerciseLog? {
        return database
            .exerciseLogQueries
            .selectLatestExerciseLogByWorkoutPlan(workoutPlanId)
            .executeAsOneOrNull()?.toDomain()

    }

    override suspend fun getAllExerciseLog(): List<ExerciseLog> {
        return database
            .exerciseLogQueries
            .selectAllExerciseLog()
            .executeAsList()
            .map { it.toDomain() }
    }

    override suspend fun getExerciseLogByDateTimeRange(startDateTime: Long, endDateTime: Long): List<ExerciseLog> {
        return database
            .exerciseLogQueries
            .selectExerciseLogByDateTimeStampRange(startDateTime, endDateTime)
            .executeAsList()
            .map { it.toDomain() }
    }

    override suspend fun getExerciseLogByExerciseId(exerciseId: Long): Flow<List<ExerciseLog>> {
        return database
            .exerciseLogQueries
            .selectAllExerciseLogById(exerciseId = exerciseId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map {
                logs -> logs.map {
                    it.toDomain()
                }
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

    private fun ExerciseLogEntity.toDomain(): ExerciseLog {
        return ExerciseLog(
            id = this.id,
            exerciseId = this.exerciseId,
            workoutPlanId = this.workoutPlanId,
            reps = this.reps.toInt(),
            weight = this.weight,
            measurement = this.measurement,
            setNumberOrder = this.setNumberOrder.toInt(),
            finishedDateTime = this.finishedDateTime,
            logNotes = this.logNotes.orEmpty(),
        )
    }
}