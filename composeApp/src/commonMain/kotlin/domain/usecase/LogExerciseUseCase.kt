package domain.usecase

import domain.repository.IGymRepository

class LogExerciseUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(
        workoutPlanExerciseId: Long,
        workoutPlanId: Long,
        exerciseId: Long,
        reps: Int,
        weight: Int
    ): Boolean {
        return repository.logExercise(workoutPlanExerciseId, workoutPlanId, exerciseId, reps, weight)
    }
}