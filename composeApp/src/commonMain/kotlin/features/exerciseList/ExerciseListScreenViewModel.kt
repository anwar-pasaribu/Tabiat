package features.exerciseList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.Exercise
import domain.usecase.GetExerciseListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseListScreenViewModel(
    private val getExerciseListUseCase: GetExerciseListUseCase
): ViewModel() {

    private val _exerciseList = MutableStateFlow<List<Exercise>>(emptyList())
    val exerciseList: StateFlow<List<Exercise>> = _exerciseList.asStateFlow()

    fun loadExerciseList() {
        viewModelScope.launch {
            val result = getExerciseListUseCase()
            _exerciseList.value = result
        }
    }

}