package domain.model.gym

data class GymPreferences(
    val setTimerDuration: Int,
    val breakTimeDuration: Int
) {
    companion object {
        val DefaultGymPreferences = GymPreferences (
            0,0
        )
    }
}
