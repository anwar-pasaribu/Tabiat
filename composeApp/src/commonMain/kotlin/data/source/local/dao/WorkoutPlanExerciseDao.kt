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
import com.unwur.tabiatmu.database.TabiatDatabase
import com.unwur.tabiatmu.database.WorkoutPlanExerciseEntity
import domain.model.gym.WorkoutPlanExercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkoutPlanExerciseDao(
    private val database: TabiatDatabase,
) : IWorkoutPlanExerciseDao {
    override suspend fun getAllWorkoutPlanExerciseObservable(workoutPlanId: Long): Flow<List<WorkoutPlanExercise>> {
        return database
            .workoutPlanExerciseQueries
            .selectAllWorkoutPlanExercise(workoutPlanId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { woPlanExerciseEntityList ->
                woPlanExerciseEntityList.map {
                    it.toDomain()
                }
            }
    }

    override suspend fun getAllWorkoutPlanExercise(workoutPlanId: Long): List<WorkoutPlanExercise> {
        return database
            .workoutPlanExerciseQueries
            .selectAllWorkoutPlanExercise(workoutPlanId)
            .executeAsList()
            .map {
                it.toDomain()
            }
    }

    override suspend fun selectWorkoutPlanExerciseByWorkoutPlanIdAndExerciseId(
        workoutPlanId: Long,
        exerciseId: Long,
    ): List<WorkoutPlanExercise> {
        return database
            .workoutPlanExerciseQueries
            .selectWorkoutPlanExerciseByWorkoutPlanIdAndExerciseId(
                workoutPlanId,
                exerciseId,
            )
            .executeAsList()
            .map { it.toDomain() }
    }

    private fun WorkoutPlanExerciseEntity.toDomain(): WorkoutPlanExercise {
        return WorkoutPlanExercise(
            this.id,
            this.exerciseId,
            this.workoutPlanId,
            this.reps.toInt(),
            this.weight.toInt(),
            this.setNumberOrder.toInt(),
            this.finishedDateTime,
        )
    }

    override suspend fun getLatestWorkoutPlanExercise(): WorkoutPlanExercise {
        return database
            .workoutPlanExerciseQueries
            .selectLatestWorkoutPlanExercise { id, exerciseId, workoutPlanId, reps, weight, setNumberOrder, finishedDateTime ->
                WorkoutPlanExercise(
                    id,
                    exerciseId,
                    workoutPlanId,
                    reps.toInt(),
                    weight.toInt(),
                    setNumberOrder.toInt(),
                    finishedDateTime,
                )
            }.executeAsOne()
    }

    override suspend fun insertWorkoutPlanExercise(
        exerciseId: Long,
        workoutPlanId: Long,
        reps: Long,
        weight: Long,
        setNumberOrder: Long,
        finishedDateTime: Long,
    ) {
        database
            .workoutPlanExerciseQueries.insertWorkoutPlanExercise(
                exerciseId,
                workoutPlanId,
                reps,
                weight,
                setNumberOrder,
                finishedDateTime,
            )
    }

    override suspend fun deleteWorkoutPlanExerciseByWorkoutPlanIdAndExerciseId(
        workoutPlanId: Long,
        exerciseId: Long,
    ) {
        database
            .workoutPlanExerciseQueries
            .deleteWorkoutPlanExerciseByWorkoutPlanIdAndExerciseId(
                workoutPlanId = workoutPlanId,
                exerciseId = exerciseId,
            )
    }

    override suspend fun deleteWorkoutPlanExerciseSet(workoutPlanExerciseId: Long) {
        database
            .workoutPlanExerciseQueries
            .deleteWorkoutPlanExerciseById(
                workoutPlanExerciseId,
            )
    }

    override suspend fun updateWorkoutPlanExerciseFinishedDateTime(
        workoutPlanExerciseId: Long,
        finishedDateTime: Long,
        reps: Long,
        weight: Long,
    ) {
        database
            .workoutPlanExerciseQueries
            .updateWorkoutPlanExerciseFinishedDateTime(
                finishedDateTime = finishedDateTime,
                reps = reps,
                weight = weight,
                id = workoutPlanExerciseId,
            )
    }

    override suspend fun updateWorkoutPlanExerciseRepsAndWeight(
        workoutPlanExerciseId: Long,
        reps: Long,
        weight: Long,
    ) {
        database
            .workoutPlanExerciseQueries
            .updateWorkoutPlanExerciseDetail(
                id = workoutPlanExerciseId,
                reps = reps,
                weight = weight,
            )
    }
}
