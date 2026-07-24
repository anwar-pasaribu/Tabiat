package com.unwur.tabiatmu.features.workoutPlanDetail.model

import com.unwur.tabiatmu.domain.model.gym.WorkoutPlan

data class WorkoutPlanDetailUiData(
    val workoutPlan: WorkoutPlan,
    val colorTheme: String
)
