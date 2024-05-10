package domain.repository

import domain.model.gym.Exercise
import domain.model.gym.Workout

interface IGymRepository {
    suspend fun logWorkout(workout: Workout): Boolean
    suspend fun getWorkouts(): List<Workout>
    suspend fun getExercises(): List<Exercise>
}