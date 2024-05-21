package domain.model.gym

data class WorkoutPlanProgress(
    val workoutPlan: WorkoutPlan,
    val total: Int,
    val progress: Int
)
