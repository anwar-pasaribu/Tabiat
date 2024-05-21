package features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.GymPreferences
import domain.repository.IGymRepository
import domain.usecase.GetGymPreferencesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingScreenViewModel(
    private val repository: IGymRepository,
    private val getGymPreferencesUseCase: GetGymPreferencesUseCase
): ViewModel() {

    private val _gymPreferences = MutableStateFlow(GymPreferences(0, 0))
    val gymPreferences: StateFlow<GymPreferences> = _gymPreferences.asStateFlow()

    val exerciseTimerOptions: StateFlow<List<GymPreferences>> = MutableStateFlow(listOf(
        GymPreferences(setTimerDuration = 15, breakTimeDuration = 5),
        GymPreferences(setTimerDuration = 30, breakTimeDuration = 7),
        GymPreferences(setTimerDuration = 45, breakTimeDuration = 9),
        GymPreferences(setTimerDuration = 60, breakTimeDuration = 10),
    )).asStateFlow()

    fun loadGymPreferences() {
        viewModelScope.launch {
            getGymPreferencesUseCase().collect {
                _gymPreferences.value = it.copy(
                    setTimerDuration = it.setTimerDuration,
                    breakTimeDuration = (it.breakTimeDuration/60)
                )
            }
        }
    }

    fun saveExerciseTimerDuration(durationSeconds: Int) {
        viewModelScope.launch {
            repository.saveExerciseSetTimerDuration(durationSeconds)
        }
    }

    fun saveBreakTimeDuration(durationMinutes: Int) {
        viewModelScope.launch {
            val minutesInSeconds = durationMinutes * 60
            repository.saveBreakTimeDuration(minutesInSeconds)
        }
    }

}