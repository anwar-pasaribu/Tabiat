package domain.usecase

import domain.model.gym.Exercise
import domain.repository.IGymRepository

class SearchExerciseUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(searchQuery: String): List<Exercise> {
        return repository.searchExercises(searchQuery)
    }
}