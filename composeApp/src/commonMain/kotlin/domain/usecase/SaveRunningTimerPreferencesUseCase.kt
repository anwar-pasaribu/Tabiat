package domain.usecase

import domain.repository.IGymRepository

class SaveRunningTimerPreferencesUseCase(
    private val repository: IGymRepository
) {
    operator suspend fun invoke(duration: Int) {
        return repository.saveRunningTimerDuration(duration)
    }
}