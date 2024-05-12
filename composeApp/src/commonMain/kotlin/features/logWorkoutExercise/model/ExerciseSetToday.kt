package features.logWorkoutExercise.model

data class ExerciseSetToday(
    val setOrder: Int,
    val repsCount: Int,
    val weight: Int,
    val finished: Boolean
)
