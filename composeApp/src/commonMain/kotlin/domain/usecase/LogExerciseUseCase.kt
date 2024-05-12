package domain.usecase

import domain.model.gym.Exercise
import domain.repository.IGymRepository

class LogExerciseUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(
        workoutPlanId: Long,
        exerciseId: Long,
        reps: Int,
        weight: Int
    ): Boolean {
        return repository.logExercise(workoutPlanId, exerciseId, reps, weight)
    }
}