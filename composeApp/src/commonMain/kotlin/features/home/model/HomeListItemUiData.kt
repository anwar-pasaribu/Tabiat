package features.home.model

data class HomeListItemUiData(
    val workoutPlanId: Long,
    val title: String,
    val description: String,
    val exerciseImageUrl: String,
    val lastActivityDate: String,
    val lastActivityDetail: String,
    val total: Int,
    val progress: Int,
)