/*
 * MIT License
 *
 * Copyright (c) 2024 Anwar Pasaribu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Project Name: Tabiat
 */
package data.source.remote.api

import data.source.remote.entity.ExerciseDto
import domain.constant.GITHUB_GYM_DATABASE_BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GymApi(
    private val httpClient: HttpClient,
) : IGymApi {
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
