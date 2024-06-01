package data.source.local.dao

import domain.model.gym.Exercise
import kotlinx.coroutines.flow.Flow

interface IExerciseDao {
    suspend fun exerciseListCount(): Long
    suspend fun getAllExerciseCategories(): List<String>
    suspend fun getAllExercises(): List<Exercise>
    fun getAllExercisesObservable(): Flow<List<Exercise>>
    suspend fun searchExercises(searchQuery: String): List<Exercise>
    suspend fun filterExercisesByTargetMuscle(targetMuscle: String): List<Exercise>

    suspend fun getExerciseById(id: Long): Exercise?

    suspend fun insertExercise(
        name: String,
        type: Long,
        difficulty: Long,
        equipment: String,
        instructions: String,
        video: String,
        image: String,
        targetMuscle: String,
        description: String,
    )

    suspend fun deleteAllExerciseData()
}