package domain.model.gym

data class ExerciseProgress(
    val exercise: Exercise,
    val sessionTotal: Int,
    val sessionDoneCount: Int
)
