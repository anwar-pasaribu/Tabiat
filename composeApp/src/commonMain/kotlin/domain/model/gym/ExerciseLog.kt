package domain.model.gym

data class ExerciseLog(
    val id: Long,
    val exerciseId: Long,
    val workoutPlanId: Long,
    val reps: Int,
    val weight: Int,
    val measurement: String,
    val setNumberOrder: Int,
    val dateTimeStamp: Long,
    val logNotes: String,
)
