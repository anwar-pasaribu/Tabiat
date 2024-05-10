package domain.usecase

import domain.model.gym.Exercise

class GetExerciseListUseCase {
    suspend operator fun invoke(): List<Exercise> {
        return emptyList()
    }
}