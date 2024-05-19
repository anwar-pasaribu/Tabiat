package domain.usecase

import domain.model.gym.Exercise
import domain.repository.IGymRepository
import kotlinx.coroutines.flow.Flow

class GetExerciseListByWorkoutPlanUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(workoutPlanId: Long): Flow<List<Exercise>> {
        return repository.getWorkoutPlanExercisesObservable(workoutPlanId)
    }
}