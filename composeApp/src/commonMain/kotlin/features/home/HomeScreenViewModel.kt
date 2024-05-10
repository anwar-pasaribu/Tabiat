package features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.Workout
import domain.repository.IGymRepository
import domain.usecase.GetWorkoutListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val gymRepository: IGymRepository,
    private val getWorkoutListUseCase: GetWorkoutListUseCase
) : ViewModel() {

    private val _workoutListStateFlow = MutableStateFlow(emptyList<Workout>())
    val workoutListStateFlow: StateFlow<List<Workout>> = _workoutListStateFlow.asStateFlow()

    fun loadWorkoutList() {
        viewModelScope.launch {
            val workoutList = getWorkoutListUseCase.invoke()
            _workoutListStateFlow.value = workoutList
        }
    }
}