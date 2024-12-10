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
import domain.model.gym.WorkoutPlan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkoutPlanDao(
    private val database: TabiatDatabase,
) : IWorkoutPlanDao {
    override fun getAllWorkoutPlanObservable(): Flow<List<WorkoutPlan>> {
        return database.workoutPlanQueries.selectAllWorkoutPlan()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { workoutPlanEntityList ->
                workoutPlanEntityList.map {
                    WorkoutPlan(
                        id = it.id,
                        name = it.name,
                        description = it.description,
                        dateTimeStamp = it.dateTimeStamp,
                        orderingNumber = it.orderingNumber.toInt(),
                    )
                }
            }
    }

    override suspend fun getAllWorkoutPlan(): List<WorkoutPlan> {
        return database.workoutPlanQueries.selectAllWorkoutPlan()
            .executeAsList()
            .map {
                WorkoutPlan(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    dateTimeStamp = it.dateTimeStamp,
                    orderingNumber = it.orderingNumber.toInt(),
                )
            }
    }

    override suspend fun getWorkoutPlan(workoutPlanId: Long): WorkoutPlan {
        try {
            val data = database.workoutPlanQueries.selectWorkoutPlanById(workoutPlanId).executeAsOne()
            return WorkoutPlan(
                id = data.id,
                name = data.name,
                description = data.description,
                dateTimeStamp = data.dateTimeStamp,
                orderingNumber = data.orderingNumber.toInt(),
            )
        } catch (e: Exception) {
            return WorkoutPlan(
                id = 0,
                name = "",
                description = "",
                dateTimeStamp = 0,
                orderingNumber = 0,
            )
        }
    }

    override suspend fun getLatestWorkoutPlan(): WorkoutPlan {
        try {
            val data = database.workoutPlanQueries.selectLatestWorkoutPlan().executeAsOne()
            return WorkoutPlan(
                id = data.id,
                name = data.name,
                description = data.description,
                dateTimeStamp = data.dateTimeStamp,
                orderingNumber = data.orderingNumber.toInt(),
            )
        } catch (e: Exception) {
            return WorkoutPlan(
                id = 0,
                name = "",
                description = "",
                dateTimeStamp = 0,
                orderingNumber = 0,
            )
        }
    }

    override suspend fun insertWorkoutPlan(
        name: String,
        description: String,
        datetimeStamp: Long,
        orderingNumber: Int,
    ) {
        database.workoutPlanQueries.insertWorkoutPlan(
            name = name,
            description = description,
            dateTimeStamp = datetimeStamp,
            orderingNumber = orderingNumber.toLong(),
        )
    }

    override suspend fun updateWorkoutPlan(
        workoutPlanId: Long,
        name: String,
        description: String,
    ) {
        val existingWorkoutPlan = getWorkoutPlan(workoutPlanId)
        if (existingWorkoutPlan.id > 0) {
            database.workoutPlanQueries.updateWorkoutPlan(
                id = workoutPlanId,
                name = name,
                description = description,
                orderingNumber = existingWorkoutPlan.orderingNumber.toLong(),
            )
        }
    }

    override suspend fun deleteWorkoutPlan(workoutPlanId: Long) {
        database.workoutPlanQueries.deleteWorkoutPlanById(workoutPlanId)
    }
}
