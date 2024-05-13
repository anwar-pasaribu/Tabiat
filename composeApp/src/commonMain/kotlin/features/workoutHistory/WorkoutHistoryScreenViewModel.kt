package features.workoutHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.ExerciseLog
import domain.usecase.GetExerciseLogListByDateTimeStampUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.todayIn
import kotlinx.datetime.until
import features.workoutHistory.model.DayCalendarData
import features.workoutHistory.model.MonthCalendarData

class WorkoutHistoryScreenViewModel(
    private val getExerciseLogListByDateTimeStampUseCase: GetExerciseLogListByDateTimeStampUseCase,
) : ViewModel() {

    val selectedTimeStamp = MutableStateFlow(Clock.System.now().toEpochMilliseconds())

    private var _exerciseLogList = MutableStateFlow<List<ExerciseLog>>(emptyList())
    val exerciseLogList = _exerciseLogList.asStateFlow()

    val calenderListStateFlow = MutableStateFlow(emptyList<MonthCalendarData>())

    fun getExerciseLogList(dateTimeStamp: Long) {
        viewModelScope.launch {
            _exerciseLogList.value = getExerciseLogListByDateTimeStampUseCase(dateTimeStamp)
        }
    }

    fun loadCalenderData() {
        viewModelScope.launch {

            withContext(Dispatchers.Default) {
                val tz = TimeZone.currentSystemDefault()
                val today: LocalDate = Clock.System.todayIn(tz)
                val calendarEmojis = mutableListOf<MonthCalendarData>()

                for (monthNum in 1..today.monthNumber) {

                    val start = LocalDate(year = today.year, monthNumber = monthNum, dayOfMonth = 1)
                    val end = start.plus(1, DateTimeUnit.MONTH)
                    val totalDayCountInTheMonth = start.until(end, DateTimeUnit.DAY)

                    val dailyEmojiList = ArrayList<DayCalendarData>(totalDayCountInTheMonth)

                    for (day in start.dayOfMonth..totalDayCountInTheMonth) {

                        val dayLocalDate = LocalDate(
                            year = today.year,
                            monthNumber = monthNum,
                            dayOfMonth = day
                        )

                        val isFutureDate = dayLocalDate > today

                        val exerciseLogList = getExerciseLogListByDateTimeStampUseCase.invoke(
                            dayLocalDate.toEpochTimeStamp()
                        )

                        dailyEmojiList.add(
                            DayCalendarData(
                                day = dayLocalDate,
                                isFutureDate = isFutureDate,
                                exerciseActivityCount = exerciseLogList.size
                            )
                        )
                    }
                    calendarEmojis.add(
                        MonthCalendarData(
                            month = start,
                            dailyDataList = dailyEmojiList.toImmutableList()
                        )
                    )
                }

                calenderListStateFlow.emit(calendarEmojis)
            }
        }

    }

    fun setSelectedDate(localDate: LocalDate) {
        val tz = TimeZone.currentSystemDefault()
        val endOfTimeStampOfSelectedDate = LocalTime(hour = 23, minute = 59, second = 59)
        val dateTime = LocalDateTime(date = localDate, time = endOfTimeStampOfSelectedDate)
        val untilTimeStampMillis = dateTime.toInstant(tz).toEpochMilliseconds()

        selectedTimeStamp.value = untilTimeStampMillis
    }

    private fun LocalDate.toEpochTimeStamp(): Long {
        val tz = TimeZone.currentSystemDefault()
        val endOfTimeStampOfSelectedDate = LocalTime(hour = 23, minute = 59, second = 59)
        val dateTime = LocalDateTime(date = this, time = endOfTimeStampOfSelectedDate)
        return dateTime.toInstant(tz).toEpochMilliseconds()
    }
}