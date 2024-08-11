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
package di

import data.source.remote.api.GymApi
import data.source.remote.api.IGymApi
import domain.constant.GITHUB_GYM_DATABASE_BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun getNetworkModule() = module {
    single {
        httpClient
    }

    single<IGymApi> {
        GymApi(httpClient)
    }
}

val httpClient = HttpClient {
    // https://raw.githubusercontent.com/anwar-pasaribu/free-exercise-db/main/dist/exercises.json
    // https://github.com/anwar-pasaribu/free-exercise-db/blob/main/exercises/3_4_Sit-Up/0.jpg?raw=true
    // https://raw.githubusercontent.com/anwar-pasaribu/free-exercise-db/main/exercises/3_4_Sit-Up/0.jpg
    // https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Air_Bike/0.jpg
    defaultRequest {
        url(GITHUB_GYM_DATABASE_BASE_URL)
    }

    install(Logging) {
        logger = Logger.DEFAULT
        level = io.ktor.client.plugins.logging.LogLevel.ALL
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 10000
    }
    install(ContentNegotiation) {
        json(json = Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
    }
}
