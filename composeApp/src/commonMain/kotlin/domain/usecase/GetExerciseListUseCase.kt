package domain.usecase

import domain.constant.GITHUB_GYM_DATABASE_BASE_URL
import domain.model.gym.Exercise
import domain.repository.IGymRepository
import kotlinx.serialization.json.Json

class GetExerciseListUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(): List<Exercise> {
        return repository.getExercises().map {
            // https://raw.githubusercontent.com/anwar-pasaribu/free-exercise-db/main/exercises/3_4_Sit-Up/0.jpg
            val image1 = Json.decodeFromString<List<String>>(it.image).getOrNull(0).orEmpty()
            val imageUrl = "${GITHUB_GYM_DATABASE_BASE_URL}main/exercises/$image1"
            it.copy(
                image = imageUrl
            )
        }
    }
}