package features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.WorkoutPlan
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

    private val _workoutListStateFlow = MutableStateFlow(emptyList<WorkoutPlan>())
    val workoutListStateFlow: StateFlow<List<WorkoutPlan>> = _workoutListStateFlow.asStateFlow()

    fun loadWorkoutList() {
        viewModelScope.launch {
            getWorkoutPlanListUseCase.invoke().collect {
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