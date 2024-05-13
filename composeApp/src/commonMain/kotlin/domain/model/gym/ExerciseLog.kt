package domain.model.gym

data class ExerciseLog(
    val id: Long,
    val exerciseId: Long,
    val workoutPlanId: Long,
    val reps: Int,
    val weight: Double,
    val measurement: String,
    val setNumberOrder: Int,
    val finishedDateTime: Long,
    val logNotes: String,
)
