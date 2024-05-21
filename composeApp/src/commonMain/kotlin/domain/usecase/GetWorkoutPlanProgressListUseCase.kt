package domain.usecase

import domain.model.gym.WorkoutPlanProgress
import domain.repository.IGymRepository
import kotlinx.coroutines.flow.Flow

class GetWorkoutPlanProgressListUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(): Flow<List<WorkoutPlanProgress>> {
        return repository.getWorkoutPlanProgressListObservable()
    }
}