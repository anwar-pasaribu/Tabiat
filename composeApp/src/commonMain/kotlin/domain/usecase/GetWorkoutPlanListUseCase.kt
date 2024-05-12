package domain.usecase

import domain.model.gym.WorkoutPlan
import domain.repository.IGymRepository

class GetWorkoutPlanListUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(): List<WorkoutPlan> {
        return repository.getWorkoutPlans()
    }
}