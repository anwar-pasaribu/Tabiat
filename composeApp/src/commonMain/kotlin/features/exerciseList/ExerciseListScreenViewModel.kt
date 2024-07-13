package features.exerciseList

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.Exercise
import domain.usecase.FilterExerciseByTargetMuscleCategoryUseCase
import domain.usecase.GetExerciseListUseCase
import domain.usecase.GetListExerciseCategoryUseCase
import domain.usecase.SearchExerciseUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
sealed class ExerciseListUiState {
    data object Loading: ExerciseListUiState()
    data object Error: ExerciseListUiState()
    data class Success(val list: List<Exercise>): ExerciseListUiState()
    data class Search(val queryString: String, val list: List<Exercise>): ExerciseListUiState()
    data class Filter(val list: List<Exercise>): ExerciseListUiState()
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
        _uiState.update {
            ExerciseListUiState.Loading
        }
        viewModelScope.launch {
            delay(300)
            getExerciseListUseCase.invoke()
                .catch {
                    _uiState.update {
                        ExerciseListUiState.Error
                    }
                }
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
                getListExerciseCategoryUseCase.invoke().map { it.toUiDisplay() }
            }
        }
    }

    private fun String.toUiDisplay(): String {
        return muscleMap.getOrElse(this) { "-" }
    }

    private val muscleMap: Map<String, String> = mapOf(
        "quadriceps" to "quadrisep (paha)",
        "shoulders" to "bahu",
        "abdominals" to "perut",
        "chest" to "dada",
        "hamstrings" to "hamstring (paha blk)",
        "triceps" to "trisep (lengan bawah)",
        "biceps" to "bisep (lengan atas)",
        "lats" to "latissimus (punggung)",
        "middle back" to "punggung tengah",
        "calves" to "betis",
        "lower back" to "punggung bawah",
        "forearms" to "lengan bawah",
        "glutes" to "bokong",
        "traps" to "trapezius (punggung)",
        "adductors" to "adduktor (paha dlm)",
        "abductors" to "abduktor (paha luar)",
        "neck" to "leher"
    )

    private fun getKeyByValue(value: String): String {
        for ((key, mapValue) in muscleMap.entries) {
            if (mapValue == value) {
                return key
            }
        }
        return ""
    }

    fun searchExercise(searchQuery: String) {
        viewModelScope.launch {
            _uiState.update {
                ExerciseListUiState.Search(
                    searchQuery,
                    searchExerciseUseCase.invoke(searchQuery)
                )
            }
        }
    }

    fun loadExerciseByCategory(category: String) {
        viewModelScope.launch {
            val categoryInDbVersion = getKeyByValue(category)
            _uiState.update {
                ExerciseListUiState.Filter(
                    filterExerciseByTargetMuscleCategoryUseCase.invoke(categoryInDbVersion)
                )
            }
        }
    }

}