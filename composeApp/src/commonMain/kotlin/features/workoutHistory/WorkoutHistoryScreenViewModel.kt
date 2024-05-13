package features.workoutHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.ExerciseLog
import domain.model.gym.WorkoutPlanExercise
import domain.repository.IGymRepository
import domain.usecase.GetExerciseLogListByDateTimeStampUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutHistoryScreenViewModel(
    private val getExerciseLogListByDateTimeStampUseCase: GetExerciseLogListByDateTimeStampUseCase,
) : ViewModel() {

    private var _exerciseLogList = MutableStateFlow<List<ExerciseLog>>(emptyList())
    val exerciseLogList = _exerciseLogList.asStateFlow()

    fun getExerciseLogList(dateTimeStamp: Long) {
        viewModelScope.launch {
            _exerciseLogList.value = getExerciseLogListByDateTimeStampUseCase(dateTimeStamp)
        }
    }
}