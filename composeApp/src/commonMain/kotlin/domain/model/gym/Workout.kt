package domain.model.gym

data class Workout(
    val id: Long,
    val exerciseId: Long,
    val name: String,
    val notes: String,
    val reps: Int,
    val sets: Int,
    val weight: Int,
    val dateTimeStamp: Long
)
