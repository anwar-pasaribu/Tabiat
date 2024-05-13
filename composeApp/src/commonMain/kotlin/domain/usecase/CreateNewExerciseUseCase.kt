package domain.usecase

import domain.model.gym.Exercise
import domain.repository.IGymRepository

class CreateNewExerciseUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(exercise: Exercise): Boolean {
        return repository.createNewExercise(exercise)
    }
}