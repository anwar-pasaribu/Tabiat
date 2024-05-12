package features.navigationHelper

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class NavigationViewModel: ViewModel() {

    val currentWorkoutPlanId = MutableStateFlow(1L)
    val currentExerciseId = MutableStateFlow(1L)

}