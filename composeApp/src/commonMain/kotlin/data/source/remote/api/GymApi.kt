package data.source.remote.api

import data.source.remote.entity.ExerciseDto
import domain.constant.GITHUB_GYM_DATABASE_BASE_URL
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
            response.map {
                // Image URL Sample
                // https://raw.githubusercontent.com/anwar-pasaribu/free-exercise-db/main/exercises/3_4_Sit-Up/0.jpg
                it.images = it.images?.map { imageFileName ->
                    "${GITHUB_GYM_DATABASE_BASE_URL}main/exercises/$imageFileName"
                }.orEmpty()
            }
            return response
        } catch (e: Exception) {
            throw e
        }
    }
}