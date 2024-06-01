package data.source.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.unwur.tabiatmu.database.ExerciseEntity
import com.unwur.tabiatmu.database.TabiatDatabase
import domain.model.gym.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class ExerciseDao(
    private val database: TabiatDatabase
) : IExerciseDao {

    override suspend fun exerciseListCount(): Long {
        return database.exerciseQueries.exerciseListCount().executeAsOne()
    }

    override suspend fun getAllExerciseCategories(): List<String> {
        val rawList = database
            .exerciseQueries
            .getDistinctCategories()
            .executeAsList()
            .map {
                it.targetMuscle.orEmpty()
            }

        val categoryList = mutableListOf<String>()
        rawList.map {
            categoryList.addAll(getDecodedListFromJsonString(it))
        }
        return categoryList.distinctBy { it }
    }

    override fun getAllExercisesObservable(): Flow<List<Exercise>> {
        return database
            .exerciseQueries
            .selectAllExercise()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map {
                exerciseList -> exerciseList.map { it.toDomain() }
            }
    }


    override suspend fun getAllExercises(): List<Exercise> {
        return database
            .exerciseQueries
            .selectAllExercise()
            .executeAsList()
            .map {
                it.toDomain()
            }
    }

    override suspend fun filterExercisesByTargetMuscle(targetMuscle: String): List<Exercise> {
        return database
            .exerciseQueries
            .filterByTargetMuscle("%$targetMuscle%")
            .executeAsList()
            .map { it.toDomain() }
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

    override suspend fun deleteAllExerciseData() {
        database
            .exerciseQueries
            .deleteAllExercise()
    }

    private fun ExerciseEntity.toDomain(): Exercise {
        val imageUrlList = getDecodedListFromJsonString(this.image.orEmpty())
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

    private fun getDecodedListFromJsonString(jsonString: String): List<String> {
        return try {
            Json.decodeFromString<List<String>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }
}