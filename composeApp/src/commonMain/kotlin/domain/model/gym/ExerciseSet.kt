package domain.model.gym

data class ExerciseSet(
    val id: Long,
    val exerciseId: Long,
    val reps: Int,
    val weight: Int
)
