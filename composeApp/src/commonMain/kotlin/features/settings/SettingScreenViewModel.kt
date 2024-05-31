package features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.enums.SoundEffectType
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

    private val _gymPreferences = MutableStateFlow(GymPreferences(0, 0, SoundEffectType.BOXING_BELL))
    val gymPreferences: StateFlow<GymPreferences> = _gymPreferences.asStateFlow()

    val exerciseTimerOptions: StateFlow<List<GymPreferences>> = MutableStateFlow(listOf(
        GymPreferences(
            setTimerDuration = 15,
            breakTimeDuration = 5,
            timerSoundEffect = SoundEffectType.BOXING_BELL
        ),
        GymPreferences(
            setTimerDuration = 30,
            breakTimeDuration = 7,
            timerSoundEffect = SoundEffectType.BOXING_BELL
        ),
        GymPreferences(
            setTimerDuration = 45,
            breakTimeDuration = 9,
            timerSoundEffect = SoundEffectType.BOXING_BELL
        ),
        GymPreferences(
            setTimerDuration = 60,
            breakTimeDuration = 10,
            timerSoundEffect = SoundEffectType.BOXING_BELL
        ),
    )).asStateFlow()

    fun loadGymPreferences() {
        viewModelScope.launch {
            getGymPreferencesUseCase().collect {
                _gymPreferences.value = it.copy(
                    setTimerDuration = it.setTimerDuration,
                    breakTimeDuration = (it.breakTimeDuration/60),
                    timerSoundEffect = it.timerSoundEffect
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

    fun saveSoundEffectSelection(soundEffectType: SoundEffectType) {
        viewModelScope.launch {
            repository.saveTimerSoundType(soundEffectType.code)
        }
    }

}