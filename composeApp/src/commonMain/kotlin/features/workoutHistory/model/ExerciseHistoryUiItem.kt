package features.workoutHistory.model

import androidx.compose.runtime.Stable

@Stable
data class ExerciseHistoryUiItem(
    val exerciseLogId: Long,
    val exerciseId: Long,
    val exerciseName: String,
    val exerciseTargetMuscle: List<String>,
    val reps: Int,
    val weight: Double,
    val measurement: String,
    val finishedDateTime: Long,
    val logNotes: String,
)
