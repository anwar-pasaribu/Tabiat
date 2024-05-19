package features.inputWorkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.WorkoutPlan
import domain.repository.IGymRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InputWorkoutScreenViewModel(
    private val repository: IGymRepository
) : ViewModel() {

    private val _workoutPlan = MutableStateFlow<WorkoutPlan?>(null)
    val workoutPlan = _workoutPlan.asStateFlow()

    fun getWorkoutPlan(workoutPlanId: Long) {
        viewModelScope.launch {
            _workoutPlan.value = repository.getWorkoutPlanById(workoutPlanId)
        }
    }

    fun createNewWorkoutPlan(workoutName: String, notes: String) {
        viewModelScope.launch {
            repository.createWorkoutPlan(workoutName, notes)
        }
    }

    fun saveWorkout(workoutPlanId: Long, workoutName: String, notes: String) {
        viewModelScope.launch {
            repository.updateWorkoutPlan(workoutPlanId, workoutName, notes)
        }
    }
}