package domain.usecase

import domain.model.gym.WorkoutPlan
import domain.repository.IGymRepository
import kotlinx.coroutines.flow.Flow

class GetWorkoutPlanListUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(): Flow<List<WorkoutPlan>> {
        return repository.getWorkoutPlansObservable()
    }
}