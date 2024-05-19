package data.source.local.dao

import domain.model.gym.Exercise

interface IExerciseDao {
    suspend fun getAllExercises(): List<Exercise>
    suspend fun searchExercises(searchQuery: String): List<Exercise>

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

}