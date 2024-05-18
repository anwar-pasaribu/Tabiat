package data.source.local.dao

import com.unwur.tabiatmu.database.TabiatDatabase
import domain.model.gym.Exercise

class ExerciseDao(
    private val database: TabiatDatabase
) : IExerciseDao {
    override suspend fun getAllExercises(): List<Exercise> {
        return database
            .exerciseQueries
            .selectAllExercise()
            .executeAsList()
            .map {
                Exercise(
                    id = it.id,
                    name = it.name,
                    difficulty = it.difficulty.toInt(),
                    equipment = it.equipment.orEmpty(),
                    instructions = it.instructions.orEmpty(),
                    video = it.video.orEmpty(),
                    image = it.image.orEmpty(),
                    targetMuscle = it.targetMuscle.orEmpty(),
                    description = it.description.orEmpty()
                )
            }
    }

    override suspend fun getExerciseById(id: Long): Exercise? {
        try {

            val exerciseEntity = database
                .exerciseQueries
                .selectExerciseById(id)
                .executeAsOne()

            return Exercise(
                id = exerciseEntity.id,
                name = exerciseEntity.name,
                difficulty = exerciseEntity.difficulty.toInt(),
                equipment = exerciseEntity.equipment.orEmpty(),
                instructions = exerciseEntity.instructions.orEmpty(),
                video = exerciseEntity.video.orEmpty(),
                image = exerciseEntity.image.orEmpty(),
                targetMuscle = exerciseEntity.targetMuscle.orEmpty(),
                description = exerciseEntity.description.orEmpty()
            )
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
}