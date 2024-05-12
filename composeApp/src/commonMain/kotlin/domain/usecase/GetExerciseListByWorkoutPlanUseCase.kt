package domain.usecase

import domain.model.gym.Exercise
import domain.repository.IGymRepository

class GetExerciseListByWorkoutPlanUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(workoutPlanId: Long): List<Exercise> {
        return repository.getWorkoutPlanExercises(workoutPlanId)
    }
}