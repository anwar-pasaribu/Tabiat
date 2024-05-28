package features.navigationHelper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.usecase.GetGymPreferencesUseCase
import domain.usecase.GetRunningTimerPreferencesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class NavigationViewModel(
    private val getRunningTimerPreferencesUseCase: GetRunningTimerPreferencesUseCase,
    getGymPreferencesUseCase: GetGymPreferencesUseCase
): ViewModel() {

    var currentTimerLeftDuration: StateFlow<Int> = getRunningTimerPreferencesUseCase().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0
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

}