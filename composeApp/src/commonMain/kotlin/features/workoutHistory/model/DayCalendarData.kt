package features.workoutHistory.model

import kotlinx.datetime.LocalDate

data class DayCalendarData(
    val day: LocalDate,
    val isFutureDate: Boolean,
    val exerciseActivityCount: Int
)
