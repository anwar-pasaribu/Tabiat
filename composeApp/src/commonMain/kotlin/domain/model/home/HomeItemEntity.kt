package domain.model.home

data class HomeItemEntity(
    val workoutPlanId: Long,
    val workoutPlanName: String,
    val workoutPlanDescription: String,
    val exerciseId: Long,
    val exerciseName: String,
    val exerciseImageUrl: String,
    val totalTaskCount: Int,
    val progress: Int,
    val workoutPlanColorTheme: String,
    val lastFinishedDateTime: Long,
    val lastReps: Int,
    val lastWeight: Int,
)
