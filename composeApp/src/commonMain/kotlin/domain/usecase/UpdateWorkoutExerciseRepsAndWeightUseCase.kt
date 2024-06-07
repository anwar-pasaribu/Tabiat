package domain.usecase

import domain.repository.IGymRepository

class UpdateWorkoutExerciseRepsAndWeightUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(
        workoutPlanExerciseId: Long,
        reps: Int,
        weight: Int
    ): Boolean {
        return repository.updateWorkoutExerciseRepsAndWeight(
            workoutPlanExerciseId = workoutPlanExerciseId,
            reps = reps,
            weight = weight
        )
    }
}