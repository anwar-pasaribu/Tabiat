package domain.model.gym

data class WorkoutPlanExercise(
    val id: Long,
    val exerciseId: Long,
    val workoutPlanId: Long,
    val reps: Int,
    val weight: Int,
    val setNumberOrder: Int,
    val finishedDateTime: Long,
)
