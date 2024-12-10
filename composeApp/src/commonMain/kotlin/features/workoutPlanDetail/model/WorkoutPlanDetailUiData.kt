package features.workoutPlanDetail.model

import domain.model.gym.WorkoutPlan

data class WorkoutPlanDetailUiData(
    val workoutPlan: WorkoutPlan,
    val colorTheme: String
)
