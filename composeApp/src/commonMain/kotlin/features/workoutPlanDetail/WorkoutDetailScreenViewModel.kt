package features.workoutPlanDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.ExerciseProgress
import domain.model.gym.WorkoutPlan
import domain.repository.IGymRepository
import domain.usecase.GetExerciseListByWorkoutPlanUseCase
import domain.usecase.GetWorkoutPlanByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutDetailScreenViewModel(
    private val repository: IGymRepository,
    private val getExerciseListByWorkoutPlanUseCase: GetExerciseListByWorkoutPlanUseCase,
    private val getWorkoutPlanByIdUseCase: GetWorkoutPlanByIdUseCase
): ViewModel() {

    private val _exerciseListStateFlow = MutableStateFlow(emptyList<ExerciseProgress>())
    val exerciseListStateFlow: StateFlow<List<ExerciseProgress>> = _exerciseListStateFlow.asStateFlow()

    private val _workoutPlanStateFlow = MutableStateFlow<WorkoutPlan?>(null)
    val workoutPlanStateFlow: StateFlow<WorkoutPlan?> = _workoutPlanStateFlow.asStateFlow()

    fun loadWorkoutPlan(workoutPlanId: Long) {
        viewModelScope.launch {
            getExerciseListByWorkoutPlanUseCase(workoutPlanId).collect { exerciseList ->
                _exerciseListStateFlow.emit(exerciseList.map { exercise ->
                    repository.getWorkoutPlanExerciseProgress(
                        workoutPlanId = workoutPlanId,
                        exerciseId = exercise.id
                    )
                })
            }
        }
    }

    fun loadWorkoutPlanById(workoutPlanId: Long) {
        viewModelScope.launch {
            _workoutPlanStateFlow.value = getWorkoutPlanByIdUseCase(workoutPlanId)
        }
    }

    fun deleteExercise(workoutPlanId: Long, workoutPlanExerciseId: Long) {
        viewModelScope.launch {
            repository.deleteWorkoutPlanExerciseByWorkoutPlanIdAndExerciseId(
                workoutPlanId = workoutPlanId,
                exerciseId = workoutPlanExerciseId
            )
        }
    }
}