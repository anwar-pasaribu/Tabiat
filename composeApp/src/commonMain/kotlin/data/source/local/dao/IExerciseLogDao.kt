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

import domain.model.gym.ExerciseLog
import kotlinx.coroutines.flow.Flow

interface IExerciseLogDao {

    suspend fun getLatestExerciseLog(
        workoutPlanId: Long,
    ): ExerciseLog?

    suspend fun getAllExerciseLog(): List<ExerciseLog>

    suspend fun getExerciseLogByDateTimeRange(
        startDateTime: Long,
        endDateTime: Long,
    ): List<ExerciseLog>

    suspend fun getExerciseLogByExerciseId(
        exerciseId: Long,
    ): Flow<List<ExerciseLog>>

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
