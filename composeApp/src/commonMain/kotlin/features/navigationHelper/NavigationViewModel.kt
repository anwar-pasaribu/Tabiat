package features.navigationHelper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.usecase.GetGymPreferencesUseCase
import domain.usecase.GetRunningTimerPreferencesUseCase
import domain.usecase.SaveRunningTimerPreferencesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NavigationViewModel(
    getRunningTimerPreferencesUseCase: GetRunningTimerPreferencesUseCase,
    private val saveRunningTimerPreferencesUseCase: SaveRunningTimerPreferencesUseCase,
    getGymPreferencesUseCase: GetGymPreferencesUseCase
): ViewModel() {

    private var runningTimerCountDownStarted = false
    var currentTimerLeftDuration: StateFlow<Int> = getRunningTimerPreferencesUseCase()
        .onEach {
            startTimerAutoCountDown(it)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            initialValue = 0
        )

    var initialTimerDuration: StateFlow<Int> = getGymPreferencesUseCase()
        .map { it.setTimerDuration }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            0
        )

    var initialBreakTimeDuration: StateFlow<Int> = getGymPreferencesUseCase()
        .map { it.breakTimeDuration }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            0
        )
    val currentWorkoutPlanId = MutableStateFlow(0L)
    val currentExerciseId = MutableStateFlow(0L)

    private suspend fun startTimerAutoCountDown(timer: Int) {
        if (timer != 0 && !runningTimerCountDownStarted) {
            viewModelScope.launch {
                saveRunningTimerPreferencesUseCase.invoke(timer)
                runningTimerCountDownStarted = true
            }
        }
    }

}