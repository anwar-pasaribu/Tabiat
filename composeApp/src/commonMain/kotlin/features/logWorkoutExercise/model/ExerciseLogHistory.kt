package features.logWorkoutExercise.model

data class ExerciseLogHistory(
    val setOrder: Int,
    val repsCount: Int,
    val weight: Double,
    val dateTimestamp: Long
)
