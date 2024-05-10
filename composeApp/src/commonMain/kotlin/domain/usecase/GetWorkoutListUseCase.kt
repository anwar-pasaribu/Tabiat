package domain.usecase

import domain.model.gym.Workout
import domain.repository.IGymRepository

class GetWorkoutListUseCase(
    private val gymRepository: IGymRepository
) {
    suspend operator fun invoke(): List<Workout> {
        return gymRepository.getWorkouts()
    }
}