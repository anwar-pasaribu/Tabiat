package domain.usecase

import domain.model.gym.Exercise
import domain.repository.IGymRepository
import kotlinx.coroutines.flow.Flow

class GetExerciseListUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(): Flow<List<Exercise>> {
        return repository.getAllExercisesObservable()
    }
}