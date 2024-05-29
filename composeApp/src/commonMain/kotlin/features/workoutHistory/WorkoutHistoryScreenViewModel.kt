package features.workoutHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.usecase.GetExerciseByIdUseCase
import domain.usecase.GetExerciseLogListByDateTimeStampUseCase
import features.workoutHistory.model.DayCalendarData
import features.workoutHistory.model.ExerciseHistoryUiItem
import features.workoutHistory.model.MonthCalendarData
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
import kotlinx.serialization.json.Json

class WorkoutHistoryScreenViewModel(
    private val getExerciseLogListByDateTimeStampUseCase: GetExerciseLogListByDateTimeStampUseCase,
    private val getExerciseByIdUseCase: GetExerciseByIdUseCase
) : ViewModel() {

    val selectedTimeStamp = MutableStateFlow(Clock.System.now().toEpochMilliseconds())

    private var _exerciseLogList = MutableStateFlow<List<ExerciseHistoryUiItem>>(emptyList())
    val exerciseLogList = _exerciseLogList.asStateFlow()

    val calenderListStateFlow = MutableStateFlow(emptyList<MonthCalendarData>())

    fun getExerciseLogList(dateTimeStamp: Long) {
        viewModelScope.launch {
            _exerciseLogList.value = getExerciseLogListByDateTimeStampUseCase(dateTimeStamp).map {
                val exercise = getExerciseByIdUseCase.invoke(it.exerciseId)
                val targetMuscles = getDecodedListFromJsonString(exercise.targetMuscle).map {
                    it.toUiDisplay()
                }
                ExerciseHistoryUiItem(
                    exerciseLogId = it.id,
                    exerciseId = it.exerciseId,
                    exerciseTargetMuscle = targetMuscles,
                    exerciseName = formatExerciseName(exercise.name),
                    reps = it.reps,
                    weight = it.weight,
                    measurement = it.measurement,
                    finishedDateTime = it.finishedDateTime,
                    logNotes = it.logNotes
                )
            }
        }
    }

    private fun formatExerciseName(name: String): String {
        val pad = 17
        return if (name.length > pad) {
            name.take(pad) + "..."
        } else {
            name
        }
    }

    private fun getDecodedListFromJsonString(jsonString: String): List<String> {
        return try {
            Json.decodeFromString<List<String>>(jsonString)
        } catch (e: Exception) {
            emptyList()
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