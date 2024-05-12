package domain.usecase

import domain.model.gym.ExerciseSet
import domain.repository.IGymRepository

class InputWorkoutPlanExerciseSetListUseCase(
    private val repository: IGymRepository
) {
    suspend operator  fun invoke(workoutPlanId: Long, exerciseId: Long, exerciseSets: List<ExerciseSet>): Boolean {
        return repository.insertExerciseSetList(workoutPlanId, exerciseId, exerciseSets)
    }
}