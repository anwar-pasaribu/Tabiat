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
package domain.repository

import domain.model.detail.DetailItemEntity
import domain.model.gym.Exercise
import domain.model.gym.ExerciseLog
import domain.model.gym.ExerciseProgress
import domain.model.gym.ExerciseSet
import domain.model.gym.GymPreferences
import domain.model.gym.WorkoutPlan
import domain.model.gym.WorkoutPlanExercise
import domain.model.gym.WorkoutPlanProgress
import domain.model.home.HomeItemEntity
import kotlinx.coroutines.flow.Flow

interface IGymRepository {
    fun getPlanProgressListObservable(): Flow<List<HomeItemEntity>>

    fun getPlanExerciseListObservable(workoutPlanId: Long): Flow<List<DetailItemEntity>>

    suspend fun resetAllYesterdayActivities()
    suspend fun createWorkoutPlan(workoutName: String, notes: String): Boolean
    suspend fun updateWorkoutPlan(workoutPlanId: Long, workoutName: String, notes: String): Boolean
    suspend fun deleteWorkoutPlan(workoutPlanId: Long): Boolean
    suspend fun getWorkoutPlans(): List<WorkoutPlan>
    suspend fun getWorkoutPlansObservable(): Flow<List<WorkoutPlan>>
    fun getWorkoutPlanProgressListObservable(): Flow<List<WorkoutPlanProgress>>

    suspend fun createNewExercise(newExercise: Exercise): Boolean
    suspend fun getAllExercisesObservable(): Flow<List<Exercise>>
    suspend fun searchExercises(searchQuery: String): List<Exercise>
    suspend fun filterExercisesByTargetMuscle(targetMuscle: String): List<Exercise>

    suspend fun getWorkoutPlanExercises(workoutPlanId: Long): List<Exercise>
    suspend fun getWorkoutPlanExercisesObservable(workoutPlanId: Long): Flow<List<Exercise>>

    suspend fun getExerciseSetList(workoutPlanId: Long, exerciseId: Long): List<WorkoutPlanExercise>
    suspend fun insertExerciseSetList(
        workoutPlanId: Long,
        exerciseId: Long,
        exerciseSetList: List<ExerciseSet>,
    ): Boolean

    suspend fun getWorkoutPlanById(workoutPlanId: Long): WorkoutPlan

    suspend fun deleteWorkoutPlanExerciseByWorkoutPlanIdAndExerciseId(workoutPlanId: Long, exerciseId: Long): Boolean
    suspend fun deleteWorkoutPlanExerciseSet(workoutPlanExerciseId: Long): Boolean

    suspend fun getWorkoutPlanExerciseProgress(
        workoutPlanId: Long,
        exerciseId: Long,
    ): ExerciseProgress

    suspend fun getExerciseById(exerciseId: Long): Exercise
    suspend fun logExercise(
        workoutPlanExerciseId: Long,
        workoutPlanId: Long,
        exerciseId: Long,
        reps: Int,
        weight: Int,
    ): Boolean

    suspend fun updateWorkoutExerciseRepsAndWeight(
        workoutPlanExerciseId: Long,
        reps: Int,
        weight: Int,
    ): Boolean

    suspend fun getExerciseLogListByDateTimeStamp(dateTimeStamp: Long): List<ExerciseLog>

    suspend fun getExerciseLogCountByDateTimeStamp(dateTimeStamp: Long): Int

    suspend fun getExerciseLogListByExerciseId(exerciseId: Long): Flow<List<ExerciseLog>>

    fun getGymPreferences(): Flow<GymPreferences>
    suspend fun saveExerciseSetTimerDuration(timerDurationInSeconds: Int)
    suspend fun saveBreakTimeDuration(durationInSeconds: Int)

    suspend fun getExerciseCategories(): List<String>

    fun getRunningTimerDuration(): Flow<Int>
    suspend fun saveRunningTimerDuration(duration: Int)

    suspend fun saveTimerSoundType(soundTypeCode: Int)

    suspend fun deleteAllExerciseData()

    suspend fun generateDummyData()
}
