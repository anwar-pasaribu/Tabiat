package data.repository

import data.source.local.dao.IWorkoutPlanDao
import data.source.local.dao.IWorkoutPlanExerciseDao
import domain.model.gym.Exercise
import domain.model.gym.ExerciseSet
import domain.model.gym.WorkoutPlan
import domain.model.gym.WorkoutPlanExercise
import domain.repository.IGymRepository
import kotlinx.datetime.Clock

class GymRepositoryImpl(
    private val workoutPlanDao: IWorkoutPlanDao,
    private val workoutPlanExerciseDao: IWorkoutPlanExerciseDao
): IGymRepository {

    // region Test exercise items
    val exercise = Exercise(
        id = 1,
        name = "Push-ups",
        type = 1, // Bodyweight
        difficulty = 2, // Intermediate
        equipment = "None",
        instructions = "1. Start in a plank position with your hands shoulder-width apart.\n" +
                "2. Bend your elbows and lower your chest towards the ground.\n" +
                "3. Push back up to the starting position.",
        video = "https://example.com/push-ups.mp4",
        image = "https://example.com/push-ups.jpg",
        targetMuscle = 1, // Chest
        description = "Push-ups are a classic bodyweight exercise that targets the chest, shoulders, and triceps."
    )
    val squat = Exercise(
        id = 2,
        name = "Squats",
        type = 1, // Bodyweight
        difficulty = 2, // Intermediate
        equipment = "None",
        instructions = "1. Stand with your feet shoulder-width apart.\n" +
                "2. Bend your knees and lower your body until your thighs are parallel to the ground.\n" +
                "3. Push back up to the starting position.",
        video = "https://example.com/squats.mp4",
        image = "https://example.com/squats.jpg",
        targetMuscle = 2, // Legs
        description = "Squats are a compound exercise that targets the legs, glutes, and core."
    )

    val pullUp = Exercise(
        id = 3,
        name = "Pull-ups",
        type = 2, // Calisthenics
        difficulty = 3, // Advanced
        equipment = "Pull-up bar",
        instructions = "1. Grab a pull-up bar with an overhand grip.\n" +
                "2. Pull yourself up until your chin is over the bar.\n" +
                "3. Lower yourself back down to the starting position.",
        video = "https://example.com/pull-ups.mp4",
        image = "https://example.com/pull-ups.jpg",
        targetMuscle = 4, // Back
        description = "Pull-ups are a compound exercise that targets the back, arms, and shoulders."
    )

    val benchPress = Exercise(
        id = 4,
        name = "Bench press",
        type = 3, // Weightlifting
        difficulty = 3, // Advanced
        equipment = "Bench press",
        instructions = "1. Lie on a bench with your feet flat on the floor.\n" +
                "2. Grab the barbell with an overhand grip.\n" +
                "3. Unrack the barbell and lower it to your chest.\n" +
                "4. Press the barbell back up to the starting position.",
        video = "https://example.com/bench-press.mp4",
        image = "https://example.com/bench-press.jpg",
        targetMuscle = 1, // Chest
        description = "Bench press is a compound exercise that targets the chest, triceps, and shoulders."
    )

    val dumbbellRow = Exercise(
        id = 5,
        name = "Dumbbell row",
        type = 3, // Weightlifting
        difficulty = 2, // Intermediate
        equipment = "Dumbbells",
        instructions = "1. Stand with your feet shoulder-width apart.\n" +
                "2. Bend over at the waist and grab a dumbbell in each hand.\n" +
                "3. Row the dumbbells up to your sides.\n" +
                "4. Lower the dumbbells back down to the starting position.",
        video = "https://example.com/dumbbell-row.mp4",
        image = "https://example.com/dumbbell-row.jpg",
        targetMuscle = 4, // Back
        description = "Dumbbell row is a compound exercise that targets the back, biceps, and shoulders."
    )

    val bicepCurl = Exercise(
        id = 6,
        name = "Bicep curl",
        type = 3, // Weightlifting
        difficulty = 1, // Beginner
        equipment = "Dumbbells",
        instructions = "1. Stand with your feet shoulder-width apart.\n" +
                "2. Hold a dumbbell in each hand.\n" +
                "3. Curl the dumbbells up to your shoulders.\n" +
                "4. Lower the dumbbells back down to the starting position.",
        video = "https://example.com/bicep-curl.mp4",
        image = "https://example.com/bicep-curl.jpg",
        targetMuscle = 3, // Arms
        description = "Bicep curl is an isolation exercise that targets the biceps."
    )
    //endregion

    private val workoutPlan = WorkoutPlan(1L, "Workout 1", "Workout 1 Description", 0L, 0)
    private val workoutPlanExercise = WorkoutPlanExercise(1L, exercise.id, workoutPlan.id, 12, 16, 1, 0L)
    private val workoutPlans: MutableList<WorkoutPlanExercise> = mutableListOf(workoutPlanExercise)
    private val exerciseList: MutableList<Exercise> = mutableListOf(exercise, squat, pullUp, benchPress, dumbbellRow, bicepCurl)
    private val workoutList: MutableList<WorkoutPlan> = mutableListOf(workoutPlan)

    override suspend fun createWorkoutPlan(workoutName: String, notes: String): Boolean {
        val latestWoPlan = workoutPlanDao.getLatestWorkoutPlan()
        workoutPlanDao.insertWorkoutPlan(
            name = workoutName,
            description = notes,
            datetimeStamp = Clock.System.now().toEpochMilliseconds(),
            orderingNumber = latestWoPlan.orderingNumber + 1
        )
        return true
    }

    override suspend fun getWorkoutPlanExercises(workoutPlanId: Long): List<Exercise> {
        val workoutPlanExercises = workoutPlanExerciseDao.getAllWorkoutPlanExercise(workoutPlanId)
        val exercises = mutableListOf<Exercise>()
        workoutPlanExercises.forEach { wpe ->
            if (exerciseList.find { it.id == wpe.exerciseId } != null) {
                exercises.add(exerciseList.find { it.id == wpe.exerciseId }!!)
            }
        }
        return exercises.distinctBy { it.id }
    }

    override suspend fun logExercise(
        workoutPlanExerciseId: Long,
        workoutPlanId: Long,
        exerciseId: Long,
        reps: Int,
        weight: Int
    ): Boolean {
//        val itemToUpdate = workoutPlans.find { it.exerciseId == exerciseId }
//        val itemToUpdateIndex = workoutPlans.indexOf(itemToUpdate)
//        if (itemToUpdateIndex != -1) {
//            itemToUpdate?.let {
//                workoutPlans[itemToUpdateIndex] = it.copy(finishedDateTime = 1)
//            }
//        }
        workoutPlanExerciseDao.updateWorkoutPlanExerciseFinishedDateTime(
            workoutPlanExerciseId = workoutPlanExerciseId,
            finishedDateTime = Clock.System.now().toEpochMilliseconds(),
            reps = reps.toLong(),
            weight = weight.toLong()
        )

        return true
    }

    override suspend fun getExerciseSetList(
        workoutPlanId: Long,
        exerciseId: Long
    ): List<WorkoutPlanExercise> {
        return workoutPlanExerciseDao.getAllWorkoutPlanExercise(workoutPlanId).filter {
            it.exerciseId == exerciseId
        }
    }

    override suspend fun getExerciseById(exerciseId: Long): Exercise {
        val exercise = exerciseList.find { it.id == exerciseId }
        return exercise ?: throw Exception("Exercise not found")
    }

    override suspend fun insertExerciseSetList(
        workoutPlanId: Long,
        exerciseId: Long,
        exerciseSetList: List<ExerciseSet>
    ): Boolean {
        exerciseSetList.forEach { exerciseSet ->
            workoutPlanExerciseDao.insertWorkoutPlanExercise(
                exerciseId = exerciseId,
                workoutPlanId = workoutPlanId,
                reps = exerciseSet.reps.toLong(),
                weight = exerciseSet.weight.toLong(),
                setNumberOrder = exerciseSet.setNumber.toLong(),
                finishedDateTime = 0L
            )
        }
        return true
    }

    override suspend fun getWorkoutPlans(): List<WorkoutPlan> {
        return workoutPlanDao.getAllWorkoutPlan()
    }

    override suspend fun getExercises(): List<Exercise> {
        return exerciseList
    }
}