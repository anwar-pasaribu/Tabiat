package domain.model.gym

public data class WorkoutPersonalization(
  public val id: Long,
  public val workoutPlanId: Long,
  public val colorTheme: String,
)
