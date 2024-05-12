package features.inputExercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.Exercise
import domain.repository.IGymRepository
import domain.usecase.GetExerciseListByWorkoutPlanUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutDetailScreenViewModel(
    private val repository: IGymRepository,
    private val getExerciseListByWorkoutPlanUseCase: GetExerciseListByWorkoutPlanUseCase,
): ViewModel() {

    private val _exerciseListStateFlow = MutableStateFlow(emptyList<Exercise>())
    val exerciseListStateFlow: StateFlow<List<Exercise>> = _exerciseListStateFlow.asStateFlow()

    fun loadWorkoutPlan(workoutPlanId: Long) {
        viewModelScope.launch {
            _exerciseListStateFlow.value = getExerciseListByWorkoutPlanUseCase(workoutPlanId)
        }
    }
}