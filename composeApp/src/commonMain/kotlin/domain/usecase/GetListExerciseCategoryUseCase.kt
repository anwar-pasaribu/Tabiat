package domain.usecase

import domain.repository.IGymRepository

class GetListExerciseCategoryUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(): List<String> {
        return repository.getExerciseCategories()
    }
}