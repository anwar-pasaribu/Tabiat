package domain.model.detail

data class DetailItemEntity(
    val workoutPlanId: Long,
    val workoutPlanName: String,
    val workoutPlanDescription: String,
    val exerciseId: Long,
    val exerciseName: String,
    val exerciseImageUrlList: List<String>,
    val totalExerciseSet: Int,
    val totalFinishedSet: Int
)
