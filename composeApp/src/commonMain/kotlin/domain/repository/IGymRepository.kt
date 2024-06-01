package domain.repository

import domain.model.gym.Exercise
import domain.model.gym.ExerciseLog
import domain.model.gym.ExerciseProgress
import domain.model.gym.ExerciseSet
import domain.model.gym.GymPreferences
import domain.model.gym.WorkoutPlan
import domain.model.gym.WorkoutPlanExercise
import domain.model.gym.WorkoutPlanProgress
import kotlinx.coroutines.flow.Flow

interface IGymRepository {
    suspend fun resetAllYesterdayActivities()
    suspend fun createWorkoutPlan(workoutName: String, notes: String): Boolean
    suspend fun updateWorkoutPlan(workoutPlanId: Long, workoutName: String, notes: String): Boolean
    suspend fun deleteWorkoutPlan(workoutPlanId: Long): Boolean
    suspend fun getWorkoutPlans(): List<WorkoutPlan>
    suspend fun getWorkoutPlansObservable(): Flow<List<WorkoutPlan>>
    suspend fun getWorkoutPlanProgressListObservable(): Flow<List<WorkoutPlanProgress>>

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
        exerciseSetList: List<ExerciseSet>
    ): Boolean

    suspend fun getWorkoutPlanById(workoutPlanId: Long): WorkoutPlan

    suspend fun deleteWorkoutPlanExerciseByWorkoutPlanIdAndExerciseId(workoutPlanId: Long, exerciseId: Long): Boolean
    suspend fun deleteWorkoutPlanExerciseSet(workoutPlanExerciseId: Long): Boolean

    suspend fun getWorkoutPlanExerciseProgress(
        workoutPlanId: Long,
        exerciseId: Long
    ): ExerciseProgress

    suspend fun getExerciseById(exerciseId: Long): Exercise
    suspend fun logExercise(
        workoutPlanExerciseId: Long,
        workoutPlanId: Long,
        exerciseId: Long,
        reps: Int,
        weight: Int
    ): Boolean

    suspend fun getExerciseLogListByDateTimeStamp(dateTimeStamp: Long): List<ExerciseLog>

    fun getGymPreferences(): Flow<GymPreferences>
    suspend fun saveExerciseSetTimerDuration(timerDurationInSeconds: Int)
    suspend fun saveBreakTimeDuration(durationInSeconds: Int)

    suspend fun getExerciseCategories(): List<String>

    fun getRunningTimerDuration(): Flow<Int>
    suspend fun saveRunningTimerDuration(duration: Int)

    suspend fun saveTimerSoundType(soundTypeCode: Int)

    suspend fun deleteAllExerciseData()
}