package features.inputExercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.ExerciseSet
import domain.usecase.InputWorkoutPlanExerciseSetListUseCase
import kotlinx.coroutines.launch

class InputExerciseScreenViewModel(
    private val inputWorkoutPlanExerciseSetListUseCase: InputWorkoutPlanExerciseSetListUseCase
): ViewModel() {

    fun insertExerciseSetList(
        workoutPlanId: Long,
        exerciseId: Long,
        exerciseSetList: List<ExerciseSet>
    ) {
        viewModelScope.launch {
            inputWorkoutPlanExerciseSetListUseCase(
                workoutPlanId = workoutPlanId,
                exerciseId = exerciseId,
                exerciseSets = exerciseSetList
            )
        }
    }

}