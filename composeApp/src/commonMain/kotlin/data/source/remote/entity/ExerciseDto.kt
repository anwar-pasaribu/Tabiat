package data.source.remote.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class ExerciseResponseDto(
    val exercises: List<ExerciseDto>
)

@Serializable
data class ExerciseDto(
    @SerialName("id") val id: String?,
    @SerialName("name") val name: String?,
    @SerialName("force") val force: String?,
    @SerialName("level") val level: String?,
    @SerialName("mechanic") val mechanic: String?,
    @SerialName("equipment") val equipment: String?,
    @SerialName("primaryMuscles") val primaryMuscles: List<String>?,
    @SerialName("secondaryMuscles") val secondaryMuscles: List<String>?,
    @SerialName("instructions") val instructions: List<String>?,
    @SerialName("category") val category: String?,
    @SerialName("images") val images: List<String>?
)

enum class DifficultyLevelDto(val levelName: String) {
    BEGINNER("beginner"),
    INTERMEDIATE("intermediate"),
    EXPERT("expert")
}
// Example usage:
//val level: DifficultyLevel = DifficultyLevel.BEGINNER
//println(level.levelName)  // Output: beginner

enum class EquipmentTypeDto(val equipmentName: String) {
    BARBELL("barbell"),
    DUMBBELL("dumbbell"),
    OTHER("other"),
    BODY_ONLY("body only"),
    CABLE("cable"),
    MACHINE("machine"),
    KETTLEBELLS("kettlebells"),
    BANDS("bands"),
    MEDICINE_BALL("medicine ball"),
    EXERCISE_BALL("exercise ball"),
    FOAM_ROLL("foam roll"),
    EZ_CURL_BAR("e-z curl bar")
}
// Example usage:
//val equipment: EquipmentType = EquipmentType.BARBELL
//println(equipment.equipmentName)  // Output: barbell

enum class MuscleGroupDto(val groupName: String) {
    QUADRICEPS("quadriceps"),
    SHOULDERS("shoulders"),
    ABDOMINALS("abdominals"),
    CHEST("chest"),
    HAMSTRINGS("hamstrings"),
    TRICEPS("triceps"),
    BICEPS("biceps"),
    LATS("lats"),
    MIDDLE_BACK("middle back"),
    CALVES("calves"),
    LOWER_BACK("lower back"),
    FOREARMS("forearms"),
    GLUTES("glutes"),
    TRAPS("traps"),
    ADDUCTORS("adductors"),
    NECK("neck")
}
// Example usage:
//val primaryMuscle: MuscleGroup = MuscleGroup.BICEPS
//println(primaryMuscle.groupName)  // Output: biceps


// Example usage:
val alternateInclineDumbbellCurl = ExerciseDto(
    id = "Alternate_Incline_Dumbbell_Curl",
    name = "Alternate Incline Dumbbell Curl",
    force = "pull",
    level = "beginner",
    mechanic = "isolation",
    equipment = "dumbbell",
    primaryMuscles = listOf("biceps"),
    secondaryMuscles = listOf("forearms"),
    instructions = listOf(
        "Sit down on an incline bench with a dumbbell in each hand being held at arms length. Tip: Keep the elbows close to the torso.This will be your starting position."
    ),
    category = "strength",
    images = listOf(
        "Alternate_Incline_Dumbbell_Curl/0.jpg",
        "Alternate_Incline_Dumbbell_Curl/1.jpg"
    )
)
