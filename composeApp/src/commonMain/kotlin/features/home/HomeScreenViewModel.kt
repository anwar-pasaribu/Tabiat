package features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.WorkoutPlanProgress
import domain.repository.IGymRepository
import domain.usecase.ResetAllYesterdayActivitiesUseCase
import features.home.model.HomeListItemUiData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

sealed class HomeScreenUiState {
    data object Loading : HomeScreenUiState()
    data object Empty : HomeScreenUiState()
    data class Success(val data: List<HomeListItemUiData>) : HomeScreenUiState()
    data class Error(val message: String) : HomeScreenUiState()
}

class HomeScreenViewModel(
    private val repository: IGymRepository,
    private val resetAllYesterdayActivitiesUseCase: ResetAllYesterdayActivitiesUseCase
) : ViewModel() {

    private val _workoutListStateFlow =
        MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    val workoutListStateFlow: StateFlow<HomeScreenUiState> = _workoutListStateFlow.asStateFlow()

    private fun WorkoutPlanProgress.toUI(): HomeListItemUiData {
        val lastActivityDateFormatted =
            this.lastExerciseLog?.finishedDateTime?.epochTimestampToShortDateTimeFormat()
        val lastExerciseSet = this.lastExerciseLog?.let {
            "${it.reps} ✕ ${it.weight}"
        }
        val formattedLastActivityDetail = lastExercise?.let {
            "${it.name.take(15)} $lastExerciseSet"
        }

        return HomeListItemUiData(
            workoutPlanId = this.workoutPlan.id,
            title = this.workoutPlan.name,
            description = this.workoutPlan.description,
            total = this.total,
            progress = this.progress,
            lastActivityDate = lastActivityDateFormatted.orEmpty(),
            lastActivityDetail = formattedLastActivityDetail.orEmpty(),
            exerciseImageUrl = this.lastExercise?.image.orEmpty()
        )
    }

    fun loadWorkoutList() {
        viewModelScope.launch {
            resetAllYesterdayActivitiesUseCase()
            repository.getWorkoutPlanProgressListObservable().collect { workoutPlanProgressList ->
                val homeList = workoutPlanProgressList.map { it.toUI() }
                if (homeList.isEmpty()) {
                    _workoutListStateFlow.emit(HomeScreenUiState.Empty)
                } else {
                    _workoutListStateFlow.emit(HomeScreenUiState.Success(homeList))
                }
            }
        }
    }

    fun deleteWorkout(workoutPlanId: Long) {
        viewModelScope.launch {
            repository.deleteWorkoutPlan(workoutPlanId)
        }
    }
}

private fun Long?.epochTimestampToShortDateTimeFormat(): String {
    if (this == null) return "--/--/--"

    // Convert epoch timestamp to LocalDateTime object
    val dateTime = Instant.fromEpochMilliseconds(
        this
    ).toLocalDateTime(TimeZone.currentSystemDefault())

    // Format the date using the desired pattern
    val dayOfMonth = dateTime.dayOfMonth.toString().padStart(2, '0')
    val month = dateTime.month.number.toString().padStart(2, '0')
    val year = dateTime.year.toString().takeLast(2)
    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')
    return "$dayOfMonth/$month/$year, $hour:$minute"
}