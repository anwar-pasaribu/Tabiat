package features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.WorkoutPlanProgress
import domain.repository.IGymRepository
import domain.usecase.GetWorkoutPlanListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val repository: IGymRepository,
    private val getWorkoutPlanListUseCase: GetWorkoutPlanListUseCase
) : ViewModel() {

    private val _workoutListStateFlow = MutableStateFlow(emptyList<WorkoutPlanProgress>())
    val workoutListStateFlow: StateFlow<List<WorkoutPlanProgress>> = _workoutListStateFlow.asStateFlow()

    fun loadWorkoutList() {
        viewModelScope.launch {
            repository.getWorkoutPlanProgressListObservable().collect {
                _workoutListStateFlow.emit(it)
            }
        }
    }

    fun deleteWorkout(workoutPlanId: Long) {
        viewModelScope.launch {
            repository.deleteWorkoutPlan(workoutPlanId)
        }
    }
}