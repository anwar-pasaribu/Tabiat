package domain.model.gym

data class ExercisePlan(
    val id: Long,
    val name: String,
    val description: String,
    val exerciseSets: List<ExerciseSet>
)
