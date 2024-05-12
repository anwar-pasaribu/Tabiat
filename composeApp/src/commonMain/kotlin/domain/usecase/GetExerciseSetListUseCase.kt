package domain.usecase

import domain.model.gym.WorkoutPlanExercise
import domain.repository.IGymRepository

class GetExerciseSetListUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(workoutPlanId: Long, exerciseId: Long): List<WorkoutPlanExercise> {
        return repository.getExerciseSetList(workoutPlanId, exerciseId)
    }
}