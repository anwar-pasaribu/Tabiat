package domain.model.gym

data class WorkoutPlan(
    val id: Long,
    val name: String,
    val description: String,
    val dateTimeStamp: Long,
    val orderingNumber: Int,
)
