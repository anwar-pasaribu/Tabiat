package domain.usecase

import domain.model.gym.Exercise

class InputExerciseUseCase {
    operator fun invoke(exercise: Exercise): Boolean {
        return true
    }
}