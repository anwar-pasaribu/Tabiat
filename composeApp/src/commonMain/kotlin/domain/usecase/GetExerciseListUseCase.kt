package domain.usecase

import domain.model.gym.Exercise
import domain.repository.IGymRepository

class GetExerciseListUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(): List<Exercise> {
        return repository.getExercises()
    }
}