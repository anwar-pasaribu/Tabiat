package features.exerciseList

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.Exercise
import domain.usecase.FilterExerciseByTargetMuscleCategoryUseCase
import domain.usecase.GetExerciseListUseCase
import domain.usecase.GetListExerciseCategoryUseCase
import domain.usecase.SearchExerciseUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
sealed class ExerciseListUiState {
    data object Loading: ExerciseListUiState()
    data class Success(val list: List<Exercise>): ExerciseListUiState()
}

class ExerciseListScreenViewModel(
    private val getExerciseListUseCase: GetExerciseListUseCase,
    private val searchExerciseUseCase: SearchExerciseUseCase,
    private val getListExerciseCategoryUseCase: GetListExerciseCategoryUseCase,
    private val filterExerciseByTargetMuscleCategoryUseCase: FilterExerciseByTargetMuscleCategoryUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<ExerciseListUiState>(ExerciseListUiState.Loading)
    val uiState: StateFlow<ExerciseListUiState> = _uiState.asStateFlow()

    val exerciseList: StateFlow<List<Exercise>> = _uiState
        .map { if (it is ExerciseListUiState.Success) it.list else emptyList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _exerciseCategoryList = MutableStateFlow<List<String>>(emptyList())
    val exerciseCategoryList: StateFlow<List<String>> = _exerciseCategoryList.asStateFlow()


    fun loadExerciseList() {
        viewModelScope.launch {
            getExerciseListUseCase()
                .collect { listExercise ->
                    _uiState.update {
                        ExerciseListUiState.Success(listExercise)
                    }
                }
        }
    }

    fun loadCategoryList() {
        viewModelScope.launch {
            _exerciseCategoryList.update {
                getListExerciseCategoryUseCase()
            }
        }
    }

    fun searchExercise(searchQuery: String) {
        viewModelScope.launch {
            _uiState.update {
                ExerciseListUiState.Success(searchExerciseUseCase(searchQuery))
            }
        }
    }

    fun loadExerciseByCategory(category: String) {
        viewModelScope.launch {
            _uiState.update {
                ExerciseListUiState.Success(filterExerciseByTargetMuscleCategoryUseCase.invoke(category))
            }
        }
    }

}