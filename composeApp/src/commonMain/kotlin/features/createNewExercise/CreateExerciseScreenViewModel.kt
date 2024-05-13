package features.createNewExercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.Exercise
import domain.model.gym.ExerciseSet
import domain.usecase.CreateNewExerciseUseCase
import domain.usecase.InputWorkoutPlanExerciseSetListUseCase
import kotlinx.coroutines.launch

class CreateExerciseScreenViewModel(
    private val createNewExerciseUseCase: CreateNewExerciseUseCase
): ViewModel() {
    fun saveExercise(exercise: Exercise) {
        viewModelScope.launch {
            createNewExerciseUseCase(
                exercise
            )
        }
    }

}