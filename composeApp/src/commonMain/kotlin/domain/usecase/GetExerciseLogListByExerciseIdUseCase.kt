package domain.usecase

import domain.model.gym.ExerciseLog
import domain.repository.IGymRepository
import kotlinx.coroutines.flow.Flow

class GetExerciseLogListByExerciseIdUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(
        exerciseId: Long
    ): Flow<List<ExerciseLog>> {
        return repository.getExerciseLogListByExerciseId(exerciseId)
    }
}