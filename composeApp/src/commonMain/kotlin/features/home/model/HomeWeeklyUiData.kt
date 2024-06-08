package features.home.model

import kotlinx.datetime.LocalDate

data class HomeWeeklyUiData(
    val upperLabel: String,
    val lowerLabel: String,
    val hasActivity: Boolean,
    val isFuture: Boolean,
    val isToday: Boolean,
    val date: LocalDate
)
