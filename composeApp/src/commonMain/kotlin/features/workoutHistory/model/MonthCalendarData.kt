package features.workoutHistory.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.LocalDate

data class MonthCalendarData(
    val month: LocalDate,
    val dailyDataList: ImmutableList<DayCalendarData>
)
