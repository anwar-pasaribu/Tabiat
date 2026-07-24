package com.unwur.tabiatmu.domain.model.personalization

data class PersonalizationRequest(
    val workoutPlanId: Long,
    val colorHexString: String
)
