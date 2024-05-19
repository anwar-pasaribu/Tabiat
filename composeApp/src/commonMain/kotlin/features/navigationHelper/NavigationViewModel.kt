package features.navigationHelper

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class NavigationViewModel: ViewModel() {

    val currentWorkoutPlanId = MutableStateFlow(0L)
    val currentExerciseId = MutableStateFlow(0L)

}