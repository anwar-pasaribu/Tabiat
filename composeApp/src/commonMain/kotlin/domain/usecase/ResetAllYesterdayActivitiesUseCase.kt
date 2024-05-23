package domain.usecase

import domain.repository.IGymRepository

class ResetAllYesterdayActivitiesUseCase(
    private val repository: IGymRepository
) {
    suspend operator fun invoke() {
        return repository.resetAllYesterdayActivities()
    }
}