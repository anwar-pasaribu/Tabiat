package domain.usecase

import domain.model.gym.Exercise
import domain.repository.IGymRepository

class GetExerciseByIdUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(exerciseId: Long): Exercise {
        return repository.getExerciseById(exerciseId)
    }
}