package data.source.local.dao

import com.unwur.tabiatmu.database.ExerciseEntity
import com.unwur.tabiatmu.database.TabiatDatabase
import domain.model.gym.Exercise
import kotlinx.serialization.json.Json

class ExerciseDao(
    private val database: TabiatDatabase
) : IExerciseDao {
    override suspend fun getAllExercises(): List<Exercise> {
        return database
            .exerciseQueries
            .selectAllExercise()
            .executeAsList()
            .map {
                it.toDomain()
            }
    }

    override suspend fun searchExercises(searchQuery: String): List<Exercise> {
        return database
            .exerciseQueries
            .searchExercise("%$searchQuery%")
            .executeAsList()
            .map {
                it.toDomain()
            }
    }

    override suspend fun getExerciseById(id: Long): Exercise? {
        try {

            val exerciseEntity = database
                .exerciseQueries
                .selectExerciseById(id)
                .executeAsOne()

            return exerciseEntity.toDomain()
        } catch (e: Exception) {
            return null
        }
    }

    override suspend fun insertExercise(
        name: String,
        type: Long,
        difficulty: Long,
        equipment: String,
        instructions: String,
        video: String,
        image: String,
        targetMuscle: String,
        description: String
    ) {

        database
            .exerciseQueries
            .insertNewExercise(
                name = name,
                type = type,
                difficulty = difficulty,
                equipment = equipment,
                instructions = instructions,
                video = video,
                image = image,
                targetMuscle = targetMuscle,
                description = description
            )
    }

    private fun ExerciseEntity.toDomain(): Exercise {
        val imageUrlList = getDecodedImageUrlList(this.image.orEmpty())
        return Exercise(
            id = this.id,
            name = this.name,
            difficulty = this.difficulty.toInt(),
            equipment = this.equipment.orEmpty(),
            instructions = this.instructions.orEmpty(),
            video = this.video.orEmpty(),
            image = imageUrlList.getOrNull(0).orEmpty(),
            imageList = imageUrlList,
            targetMuscle = this.targetMuscle.orEmpty(),
            description = this.description.orEmpty()
        )
    }

    private fun getDecodedImageUrlList(jsonString: String): List<String> {
        return try {
            Json.decodeFromString<List<String>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }
}