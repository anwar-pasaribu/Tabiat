package features.createNewExercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.Exercise
import domain.usecase.CreateNewExerciseUseCase
import domain.usecase.GetListExerciseCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateExerciseScreenViewModel(
    private val createNewExerciseUseCase: CreateNewExerciseUseCase,
    private val getListExerciseCategoryUseCase: GetListExerciseCategoryUseCase,
): ViewModel() {

    private val _exerciseMuscleTargetList = MutableStateFlow<List<String>>(emptyList())
    val exerciseMuscleTargetList: StateFlow<List<String>> = _exerciseMuscleTargetList.asStateFlow()

    fun saveExercise(exercise: Exercise) {
        viewModelScope.launch {
            createNewExerciseUseCase.invoke(
                exercise.copy(
                    targetMuscle = getKeyByValue(exercise.targetMuscle)
                )
            )
        }
    }

    fun loadMuscleGroupList() {
        viewModelScope.launch {
            _exerciseMuscleTargetList.update {
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

}