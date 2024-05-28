package domain.usecase

import domain.repository.IGymRepository
import kotlinx.coroutines.flow.Flow

class GetRunningTimerPreferencesUseCase(
    private val repository: IGymRepository
) {
    operator fun invoke(): Flow<Int> {
        return repository.getRunningTimerDuration()
    }
}