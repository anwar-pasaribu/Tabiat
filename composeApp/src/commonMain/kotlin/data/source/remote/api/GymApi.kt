package data.source.remote.api

import data.source.remote.entity.ExerciseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GymApi(
    private val httpClient: HttpClient
): IGymApi {
    override suspend fun loadExerciseList(): List<ExerciseDto> {
        try {
            val response = httpClient
                .get("main/dist/exercises.json")
                .body<List<ExerciseDto>>()
            return response
        } catch (e: Exception) {
            throw e
        }
    }
}