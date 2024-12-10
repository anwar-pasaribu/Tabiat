/*
 * MIT License
 *
 * Copyright (c) 2024 Anwar Pasaribu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Project Name: Tabiat
 */
package features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.WorkoutPlanProgress
import domain.repository.IGymRepository
import domain.usecase.GetExerciseLogListByDateTimeStampUseCase
import domain.usecase.ResetAllYesterdayActivitiesUseCase
import domain.usecase.personalization.SetWorkoutPlanPersonalizationUseCase
import features.home.model.HomeListItemUiData
import features.home.model.HomeWeeklyUiData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import ui.component.colorPalette.parseHexToComposeColor

sealed class HomeScreenUiState {
    data object Loading : HomeScreenUiState()
    data object Empty : HomeScreenUiState()
    data class Success(val data: List<HomeListItemUiData>) : HomeScreenUiState()
}

class HomeScreenViewModel(
    private val repository: IGymRepository,
    private val resetAllYesterdayActivitiesUseCase: ResetAllYesterdayActivitiesUseCase,
    private val getExerciseLogListByDateTimeStampUseCase: GetExerciseLogListByDateTimeStampUseCase,
    private val setWorkoutPlanPersonalizationUseCase: SetWorkoutPlanPersonalizationUseCase,
) : ViewModel() {

    private val _workoutListStateFlow =
        MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    val workoutListStateFlow: StateFlow<HomeScreenUiState> = _workoutListStateFlow.asStateFlow()
//    val workoutListStateFlow = repository
//        .getWorkoutPlanProgressListObservable()
//        .map { workoutPlanProgressList ->
//            val homeList = workoutPlanProgressList.map { it.toUI() }
//            when {
//                homeList.isEmpty() -> HomeScreenUiState.Empty
//                else -> HomeScreenUiState.Success(homeList)
//            }
//        }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000), // Stop sharing after 5 seconds of inactivity
//            initialValue = HomeScreenUiState.Loading
//        )

    private val _weeklyDataListStateFlow =
        MutableStateFlow<List<HomeWeeklyUiData>>(emptyList())
    val weeklyDataListStateFlow: StateFlow<List<HomeWeeklyUiData>> = _weeklyDataListStateFlow.asStateFlow()

    init {
//        viewModelScope.launch {
//            resetAllYesterdayActivitiesUseCase.invoke()
//        }
        loadWeeklyData()
    }

    private fun loadWeeklyData() {
        viewModelScope.launch {
            val weeklyUiDataList = mutableListOf<HomeWeeklyUiData>()
            val tz = TimeZone.currentSystemDefault()
            val today = Clock.System.todayIn(tz)
            val dateAtFirstOfWeek = today.minus(today.dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)
            val listOfDaysThisWeek = List(7) { index ->
                dateAtFirstOfWeek.plus(index, DateTimeUnit.DAY)
            }
            weeklyUiDataList.add(
                HomeWeeklyUiData(
                    upperLabel = today.year.toString(),
                    lowerLabel = today.month.name.take(3),
                    isFuture = false,
                    isToday = false,
                    date = today,
                    hasActivity = false,
                ),
            )
            withContext(Dispatchers.IO) {
                listOfDaysThisWeek.forEachIndexed { index, date ->
                    val isToday = date.dayOfWeek.isoDayNumber == today.dayOfWeek.isoDayNumber
                    val todayDayOfWeek = today.dayOfWeek.isoDayNumber
                    val isFuture = date.dayOfWeek.isoDayNumber > todayDayOfWeek
                    val dayOfMonth = date.dayOfMonth
                    val upperLabel = date.dayOfWeek.name
                        .take(3).lowercase().replaceFirstChar { it.uppercaseChar() }
                    val lowerLabel = dayOfMonth.toString()

                    val exerciseLogList = getExerciseLogListByDateTimeStampUseCase.invoke(
                        date.toEpochTimeStamp(),
                    )
                    weeklyUiDataList.add(
                        HomeWeeklyUiData(
                            upperLabel = upperLabel,
                            lowerLabel = lowerLabel,
                            isFuture = isFuture,
                            isToday = isToday,
                            date = date,
                            hasActivity = exerciseLogList.isNotEmpty(),
                        ),
                    )
                }
                _weeklyDataListStateFlow.value = weeklyUiDataList
            }
        }
    }

    fun getAllDayTimeStamp(localDate: LocalDate): Long {
        return localDate.toEpochTimeStamp()
    }

    private fun LocalDate.toEpochTimeStamp(): Long {
        val tz = TimeZone.currentSystemDefault()
        val endOfTimeStampOfSelectedDate = LocalTime(hour = 23, minute = 59, second = 59)
        val dateTime = LocalDateTime(date = this, time = endOfTimeStampOfSelectedDate)
        return dateTime.toInstant(tz).toEpochMilliseconds()
    }

    private fun WorkoutPlanProgress.toUI(): HomeListItemUiData {
        val lastActivityDateFormatted =
            this.lastExerciseLog?.finishedDateTime?.epochTimestampToShortDateTimeFormat()
        val lastExerciseSet = this.lastExerciseLog?.let {
            "${it.reps} ✕ ${it.weight}"
        }
        val formattedLastActivityDetail = lastExercise?.let {
            "${it.name.take(15)} $lastExerciseSet"
        }
        val parsedBackgroundColor = this.workoutPersonalization?.colorTheme.parseHexToComposeColor()

        return HomeListItemUiData(
            workoutPlanId = this.workoutPlan.id,
            title = this.workoutPlan.name,
            description = this.workoutPlan.description,
            total = this.total,
            progress = this.progress,
            lastActivityDate = lastActivityDateFormatted.orEmpty(),
            lastActivityDetail = formattedLastActivityDetail.orEmpty(),
            exerciseImageUrl = this.lastExercise?.image.orEmpty(),
            backgroundColor = parsedBackgroundColor
        )
    }

    fun loadWorkoutList() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                resetAllYesterdayActivitiesUseCase()
                repository.getWorkoutPlanProgressListObservable()
                    .collect { workoutPlanProgressList ->
                        val homeList = workoutPlanProgressList.map { it.toUI() }
                        if (homeList.isEmpty()) {
                            _workoutListStateFlow.update { HomeScreenUiState.Empty }
                        } else {
                            _workoutListStateFlow.update {
                                HomeScreenUiState.Success(homeList)
                            }
                        }
                    }
            }
        }
    }

    fun deleteWorkout(workoutPlanId: Long) {
        viewModelScope.launch {
            repository.deleteWorkoutPlan(workoutPlanId)
        }
    }

    fun changeWorkoutPlanColor(workoutPlanId: Long, colorHex: String) {
        viewModelScope.launch {
            setWorkoutPlanPersonalizationUseCase.invoke(
                personalizationRequest = domain.model.personalization.PersonalizationRequest(
                    workoutPlanId = workoutPlanId,
                    colorHexString = colorHex
                )
            )
        }
    }
}

private fun Long?.epochTimestampToShortDateTimeFormat(): String {
    if (this == null) return "--/--/--"

    // Convert epoch timestamp to LocalDateTime object
    val dateTime = Instant.fromEpochMilliseconds(
        this,
    ).toLocalDateTime(TimeZone.currentSystemDefault())

    // Format the date using the desired pattern
    val dayOfMonth = dateTime.dayOfMonth.toString().padStart(2, '0')
    val month = dateTime.month.number.toString().padStart(2, '0')
    val year = dateTime.year.toString().takeLast(2)
    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')
    return "$dayOfMonth/$month/$year, $hour:$minute"
}
