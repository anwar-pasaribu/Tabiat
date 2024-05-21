package features.logWorkoutExercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.usecase.DeleteWorkoutPlanExerciseSetUseCase
import domain.usecase.GetExerciseByIdUseCase
import domain.usecase.GetExerciseSetListUseCase
import domain.usecase.LogExerciseUseCase
import features.logWorkoutExercise.model.ExerciseSetToday
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LogWorkoutExerciseScreenViewModel(
    private val getExerciseSetListUseCase: GetExerciseSetListUseCase,
    private val getExerciseByIdUseCase: GetExerciseByIdUseCase,
    private val logExerciseUseCase: LogExerciseUseCase,
    private val deleteWorkoutPlanExerciseSetUseCase: DeleteWorkoutPlanExerciseSetUseCase,
): ViewModel() {


    private val _exerciseSetList = MutableStateFlow<List<ExerciseSetToday>>(emptyList())
    val exerciseSetList: StateFlow<List<ExerciseSetToday>> = _exerciseSetList.asStateFlow()

    private val _exerciseName = MutableStateFlow("")
    val exerciseName: StateFlow<String> = _exerciseName.asStateFlow()

    fun getExerciseDetail(exerciseId: Long) {
        viewModelScope.launch {
            _exerciseName.value = getExerciseByIdUseCase(exerciseId).name
        }
    }

    fun getExerciseSetList(workoutPlanId: Long, exerciseId: Long) {
        viewModelScope.launch {
            _exerciseSetList.value = getExerciseSetListUseCase(workoutPlanId, exerciseId).map {
                ExerciseSetToday(
                    workoutPlanExerciseId = it.id,
                    setOrder = it.setNumberOrder,
                    repsCount = it.reps,
                    weight = it.weight,
                    finished = it.finishedDateTime != 0L
                )
            }
        }
    }

    fun logExercise(
        selectedWorkoutPlanExerciseId: Long,
        workoutPlanId: Long,
        exerciseId: Long,
        reps: Int,
        weight: Int
    ) {
        viewModelScope.launch {
            logExerciseUseCase(
                workoutPlanExerciseId = selectedWorkoutPlanExerciseId,
                workoutPlanId = workoutPlanId,
                exerciseId = exerciseId,
                reps = reps,
                weight = weight
            )
            delay(1000)
            getExerciseSetList(workoutPlanId, exerciseId)
        }
    }

    fun deleteExerciseSet(
        selectedWorkoutPlanExerciseId: Long,
        workoutPlanId: Long,
        exerciseId: Long,
    ) {
        viewModelScope.launch {
            deleteWorkoutPlanExerciseSetUseCase(
                workoutPlanExerciseId = selectedWorkoutPlanExerciseId,
            )
            getExerciseSetList(workoutPlanId, exerciseId)
        }
    }

}