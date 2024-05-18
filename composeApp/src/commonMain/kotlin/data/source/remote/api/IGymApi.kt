package data.source.remote.api

import data.source.remote.entity.ExerciseDto

interface IGymApi {
    suspend fun loadExerciseList(): List<ExerciseDto>
}