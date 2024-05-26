package domain.usecase

import domain.model.gym.Exercise
import domain.repository.IGymRepository

class FilterExerciseByTargetMuscleCategoryUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(muscleTarget: String): List<Exercise> {
        return repository.filterExercisesByTargetMuscle(muscleTarget)
    }
}