package domain.usecase

import domain.model.gym.WorkoutPlan
import domain.repository.IGymRepository

class GetWorkoutPlanByIdUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(workoutPlanId: Long): WorkoutPlan {
        return repository.getWorkoutPlanById(workoutPlanId)
    }
}