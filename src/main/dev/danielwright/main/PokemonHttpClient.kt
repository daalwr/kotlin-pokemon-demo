package dev.danielwright.main

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import kotlinx.serialization.json.Json

class PokemonHttpClient {

    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }
        install(Logging) {
            level = LogLevel.NONE
        }
    }

    fun getClient(): HttpClient {
        return client
    }
}
