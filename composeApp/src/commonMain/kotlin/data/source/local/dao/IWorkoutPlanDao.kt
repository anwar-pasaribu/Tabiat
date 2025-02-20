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

import domain.model.detail.DetailItemEntity
import domain.model.gym.WorkoutPlan
import domain.model.home.HomeItemEntity
import kotlinx.coroutines.flow.Flow

interface IWorkoutPlanDao {
    fun getAllWorkoutPlanWithProgressObservable(): Flow<List<HomeItemEntity>>
    fun getAllWorkoutPlanExerciseWithProgressObservable(workoutPlanId: Long): Flow<List<DetailItemEntity>>
    fun getAllWorkoutPlanObservable(): Flow<List<WorkoutPlan>>
    suspend fun getAllWorkoutPlan(): List<WorkoutPlan>
    suspend fun getLatestWorkoutPlan(): WorkoutPlan
    suspend fun getWorkoutPlan(workoutPlanId: Long): WorkoutPlan
    suspend fun insertWorkoutPlan(
        name: String,
        description: String,
        datetimeStamp: Long,
        orderingNumber: Int,
    )
    suspend fun updateWorkoutPlan(
        workoutPlanId: Long,
        name: String,
        description: String,
    )

    suspend fun deleteWorkoutPlan(
        workoutPlanId: Long,
    )
}
