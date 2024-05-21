package domain.usecase

import domain.repository.IGymRepository

class DeleteWorkoutPlanExerciseSetUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(
        workoutPlanExerciseId: Long,
    ): Boolean {
        return repository.deleteWorkoutPlanExerciseSet(workoutPlanExerciseId)
    }
}