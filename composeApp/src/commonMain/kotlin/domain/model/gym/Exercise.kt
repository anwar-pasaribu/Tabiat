package domain.model.gym

data class Exercise(
    val id: Long,
    val name: String,
    val type: Int,
    val difficulty: Int,
    val equipment: String,
    val instructions: String,
    val video: String,
    val image: String,
    val targetMuscle: Int,
    val description: String
)
