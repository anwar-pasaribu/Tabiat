package data.repository

import domain.model.gym.Exercise
import domain.model.gym.Workout
import domain.repository.IGymRepository

class GymRepositoryImpl(): IGymRepository {

    val workout1 = Workout(
        id = 1,
        exerciseId = 1,
        name = "Bench Press",
        notes = "Felt strong today",
        reps = 8,
        sets = 3,
        weight = 225,
        dateTimeStamp = 1678901234
    )

    val workout2 = Workout(
        id = 2,
        exerciseId = 2,
        name = "Squats",
        notes = "Knees felt a bit sore",
        reps = 10,
        sets = 4,
        weight = 185,
        dateTimeStamp = 1678987654
    )

    val workout3 = Workout(
        id = 3,
        exerciseId = 3,
        name = "Pull-ups",
        notes = "Added a weight belt",
        reps = 6,
        sets = 3,
        weight = 20,
        dateTimeStamp = 1679074078
    )

    val workout4 = Workout(
        id = 4,
        exerciseId = 4,
        name = "Barbell Rows",
        notes = "Felt good, increased the weight",
        reps = 12,
        sets = 3,
        weight = 160,
        dateTimeStamp = 1679160492
    )

    val workout5 = Workout(
        id = 5,
        exerciseId = 5,
        name = "Overhead Press",
        notes = "Shoulders were a bit tight",
        reps = 8,
        sets = 3,
        weight = 115,
        dateTimeStamp = 1679246906
    )

    override suspend fun logWorkout(workout: Workout): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getWorkouts(): List<Workout> {
        return listOf(
            workout1, workout2, workout3, workout4, workout5,
        )
    }

    override suspend fun getExercises(): List<Exercise> {
        TODO("Not yet implemented")
    }
}