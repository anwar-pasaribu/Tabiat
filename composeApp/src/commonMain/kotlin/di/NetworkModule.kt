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

val httpClient = HttpClient() {
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