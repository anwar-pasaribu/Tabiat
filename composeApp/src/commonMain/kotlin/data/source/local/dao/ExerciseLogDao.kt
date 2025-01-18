/*
 * MIT License
 *
 * Copyright (c) 2024 Anwar Pasaribu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Project Name: Tabiat
 */
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
    private val database: TabiatDatabase,
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
                    logs ->
                logs.map {
                    it.toDomain()
                }
            }
    }

    override suspend fun getExerciseLogCountByDateTimeRange(
        startDateTime: Long,
        endDateTime: Long
    ): Int {
        return database.exerciseLogQueries
            .countExerciseLogByDateTimeStampRange(startDateTime, endDateTime)
            .executeAsOneOrNull()?.toInt() ?: 0
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
        longitude: Double,
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
                longitude = longitude,
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
