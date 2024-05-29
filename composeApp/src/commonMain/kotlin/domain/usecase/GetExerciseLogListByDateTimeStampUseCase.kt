package domain.usecase

import domain.model.gym.ExerciseLog
import domain.repository.IGymRepository

class GetExerciseLogListByDateTimeStampUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke(
        dateTimeStamp: Long
    ): List<ExerciseLog> {
        return repository.getExerciseLogListByDateTimeStamp(dateTimeStamp).sortedByDescending {
            it.finishedDateTime
        }
    }
}