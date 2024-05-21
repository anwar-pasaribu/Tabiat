package domain.usecase

import domain.model.gym.GymPreferences
import domain.repository.IGymRepository
import kotlinx.coroutines.flow.Flow

class GetGymPreferencesUseCase(
    private val repository: IGymRepository
) {
    operator fun invoke(): Flow<GymPreferences> {
        return repository.getGymPreferences()
    }
}