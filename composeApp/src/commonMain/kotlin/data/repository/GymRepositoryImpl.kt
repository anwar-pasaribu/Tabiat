package data.repository

import domain.model.gym.Exercise
import domain.model.gym.ExerciseSet
import domain.model.gym.Workout
import domain.model.gym.WorkoutPlan
import domain.model.gym.WorkoutPlanExercise
import domain.repository.IGymRepository

class GymRepositoryImpl(): IGymRepository {

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

    private val workoutPlan = WorkoutPlan(1L, "Workout 1", "Workout 1 Description", 0L, 0)
    private val workoutPlanExercise = WorkoutPlanExercise(1L, exercise.id, workoutPlan.id, 12, 16, 1, 0L)
    private val workoutPlans: MutableList<WorkoutPlanExercise> = mutableListOf(workoutPlanExercise)
    private val exerciseList: MutableList<Exercise> = mutableListOf(exercise)
    private val workoutList: MutableList<WorkoutPlan> = mutableListOf(workoutPlan)

    override suspend fun createWorkoutPlan(workoutName: String, notes: String): Boolean {
        return workoutList.add(
            WorkoutPlan(
                id = workoutList.last().id + 1,
                name = workoutName,
                description = notes,
                dateTimeStamp = 0L,
                orderingNumber = 0
            )
        )
    }

    override suspend fun getWorkoutPlanExercises(workoutPlanId: Long): List<Exercise> {
        val workoutPlanExercises = workoutPlans.filter { it.workoutPlanId == workoutPlanId }
        val exercises = mutableListOf<Exercise>()
        workoutPlanExercises.forEach { wpe ->
            if (exerciseList.find { it.id == wpe.exerciseId } != null) {
                exercises.add(exerciseList.find { it.id == wpe.exerciseId }!!)
            }
        }
        return exercises.distinctBy { it.id }
    }

    override suspend fun logExercise(
        workoutPlanId: Long,
        exerciseId: Long,
        reps: Int,
        weight: Int
    ): Boolean {
        val itemToUpdate = workoutPlans.find { it.exerciseId == exerciseId }
        val itemToUpdateIndex = workoutPlans.indexOf(itemToUpdate)
        if (itemToUpdateIndex != -1) {
            itemToUpdate?.let {
                workoutPlans[itemToUpdateIndex] = it.copy(finishedDateTime = 1)
            }
        }

        return true
    }

    override suspend fun getExerciseSetList(
        workoutPlanId: Long,
        exerciseId: Long
    ): List<WorkoutPlanExercise> {
        return workoutPlans.filter {
            it.workoutPlanId == workoutPlanId && it.exerciseId == exerciseId
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
            workoutPlans.add(
                WorkoutPlanExercise(
                    id = workoutPlans.last().id + 1,
                    exerciseId = exerciseId,
                    workoutPlanId = workoutPlanId,
                    reps = exerciseSet.reps,
                    weight = exerciseSet.weight,
                    setNumberOrder = exerciseSet.setNumber,
                    finishedDateTime = 0L
                )
            )
        }
        return true
    }

    override suspend fun logWorkout(workout: Workout): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getWorkoutPlans(): List<WorkoutPlan> {
        return workoutList
    }

    override suspend fun getExercises(): List<Exercise> {
        return listOf(exercise)
    }
}