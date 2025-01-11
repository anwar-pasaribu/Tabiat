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
package features.workoutHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.usecase.GetExerciseByIdUseCase
import domain.usecase.GetExerciseLogCountByDateTimeStampUseCase
import domain.usecase.GetExerciseLogListByDateTimeStampUseCase
import features.workoutHistory.model.DayCalendarData
import features.workoutHistory.model.ExerciseHistoryUiItem
import features.workoutHistory.model.MonthCalendarData
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.todayIn
import kotlinx.serialization.json.Json

sealed class WorkoutHistoryUiState {
    data object Loading : WorkoutHistoryUiState()
    data class Success(val calendarItems: List<MonthCalendarData>) : WorkoutHistoryUiState()
}
class WorkoutHistoryScreenViewModel(
    private val getExerciseLogListByDateTimeStampUseCase: GetExerciseLogListByDateTimeStampUseCase,
    private val getExerciseByIdUseCase: GetExerciseByIdUseCase,
    private val getExerciseLogCountByDateTimeStampUseCase: GetExerciseLogCountByDateTimeStampUseCase,
) : ViewModel() {

    val selectedTimeStamp = MutableStateFlow(Clock.System.now().toEpochMilliseconds())

    // Daily Log
    private var _exerciseLogList = MutableStateFlow<List<ExerciseHistoryUiItem>>(emptyList())
    val exerciseLogList = _exerciseLogList.asStateFlow()

    // Whole Calender View
    private val _historyUiState = MutableStateFlow<WorkoutHistoryUiState>(WorkoutHistoryUiState.Loading)
    val historyUiState = _historyUiState.asStateFlow()

    fun getExerciseLogList(dateTimeStamp: Long) {
        viewModelScope.launch {
            _exerciseLogList.value = getExerciseLogListByDateTimeStampUseCase(dateTimeStamp).map { exerciseLog ->
                val exercise = getExerciseByIdUseCase.invoke(exerciseLog.exerciseId)
                val targetMuscles = getDecodedListFromJsonString(exercise.targetMuscle).map {
                    it.toUiDisplay()
                }
                ExerciseHistoryUiItem(
                    exerciseLogId = exerciseLog.id,
                    exerciseId = exerciseLog.exerciseId,
                    exerciseTargetMuscle = targetMuscles,
                    exerciseName = formatExerciseName(exercise.name),
                    reps = exerciseLog.reps,
                    weight = exerciseLog.weight,
                    measurement = exerciseLog.measurement,
                    finishedDateTime = exerciseLog.finishedDateTime,
                    logNotes = exerciseLog.logNotes,
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
        "neck" to "leher",
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
            val tz = TimeZone.currentSystemDefault()
            val today: LocalDate = Clock.System.todayIn(tz)
            val calendarDataList = mutableListOf<MonthCalendarData>()

            val oneYearAgoDate = today.minus(12, DateTimeUnit.MONTH).apply {
                LocalDate(year, monthNumber, 1) // Set to the first day of the month
            }
            val firstDayOneYearAgo = LocalDate(oneYearAgoDate.year, oneYearAgoDate.monthNumber, 1)

            val yearsRangeList = List(24) { index ->
                firstDayOneYearAgo.plus(index, DateTimeUnit.MONTH)
            }

            withContext(Dispatchers.IO) {

                for (month in yearsRangeList) {
                    val end = month.plus(1, DateTimeUnit.MONTH)
                    val totalDayCountInTheMonth = month.daysUntil(end)

                    val dailyEmojiList = ArrayList<DayCalendarData>(totalDayCountInTheMonth)

                    for (day in month.dayOfMonth..totalDayCountInTheMonth) {
                        val dayLocalDate = LocalDate(
                            year = month.year,
                            monthNumber = month.monthNumber,
                            dayOfMonth = day,
                        )

                        val isFutureDate = dayLocalDate > today

                        val exerciseLogCount = getExerciseLogCountByDateTimeStampUseCase.invoke(
                            dayLocalDate.toEpochTimeStamp(),
                        )

                        dailyEmojiList.add(
                            DayCalendarData(
                                day = dayLocalDate,
                                isFutureDate = isFutureDate,
                                exerciseActivityCount = exerciseLogCount,
                            ),
                        )
                    }
                    calendarDataList.add(
                        MonthCalendarData(
                            month = month,
                            dailyDataList = dailyEmojiList.toImmutableList(),
                        ),
                    )
                }

//                for (monthNum in 1..12) {
//                    val start = LocalDate(year = today.year, monthNumber = monthNum, dayOfMonth = 1)
//                    val end = start.plus(1, DateTimeUnit.MONTH)
//                    val totalDayCountInTheMonth = start.until(end, DateTimeUnit.DAY)
//
//                    val dailyEmojiList = ArrayList<DayCalendarData>(totalDayCountInTheMonth)
//
//                    for (day in start.dayOfMonth..totalDayCountInTheMonth) {
//                        val dayLocalDate = LocalDate(
//                            year = today.year,
//                            monthNumber = monthNum,
//                            dayOfMonth = day,
//                        )
//
//                        val isFutureDate = dayLocalDate > today
//
//                        val exerciseLogList = getExerciseLogListByDateTimeStampUseCase.invoke(
//                            dayLocalDate.toEpochTimeStamp(),
//                        )
//
//                        dailyEmojiList.add(
//                            DayCalendarData(
//                                day = dayLocalDate,
//                                isFutureDate = isFutureDate,
//                                exerciseActivityCount = exerciseLogList.size,
//                            ),
//                        )
//                    }
//                    calendarDataList.add(
//                        MonthCalendarData(
//                            month = start,
//                            dailyDataList = dailyEmojiList.toImmutableList(),
//                        ),
//                    )
//                }

                _historyUiState.update {
                    WorkoutHistoryUiState.Success(calendarDataList)
                }
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
