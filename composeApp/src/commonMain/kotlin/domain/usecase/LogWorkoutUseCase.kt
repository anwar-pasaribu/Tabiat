package domain.usecase

import domain.model.gym.Workout

class LogWorkoutUseCase {
    operator fun invoke(workout: Workout): Boolean {
        return true
    }
}