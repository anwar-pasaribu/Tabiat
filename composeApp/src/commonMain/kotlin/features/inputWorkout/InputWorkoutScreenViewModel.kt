package features.inputWorkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.repository.IGymRepository
import kotlinx.coroutines.launch

class InputWorkoutScreenViewModel(
    private val repository: IGymRepository
) : ViewModel() {
    fun saveWorkout(workoutName: String, notes: String) {
        viewModelScope.launch {
            repository.createWorkoutPlan(workoutName, notes)
        }
    }
}