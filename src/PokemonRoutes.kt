package dev.danielwright

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.MDC
import kotlin.random.Random

val client = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer(Json {
            ignoreUnknownKeys = true
        })
    }
    install(Logging) {
        level = LogLevel.HEADERS
    }
}

fun Route.getPokemonRoutes() {
    get("/pokemon") {
        call.application.environment.log.info("Correlation ID is ${MDC.get("correlation-id")}")
        call.application.environment.log.info("Generating a random pokemon")
        val randomNumber: Int = Random.nextInt(1, 151)
        call.application.environment.log.info("Chosen pokemon number $randomNumber")
        val pokemon = getPokemon(randomNumber)
        call.application.environment.log.info("Pokemon $randomNumber is ${pokemon.name}")
        call.respond(pokemon)
    }
    get("/pokefail") {
        throw Exception("Pokefail exception")
    }
    authenticate("auth-basic") {
        get("/protectedpokemon") {
            call.respond("PROTECTED!")
        }
    }
}

suspend fun getPokemon(number: Int): Pokemon {
    return client.get("https://pokeapi.co/api/v2/pokemon/$number")
}

fun Application.registerPokemonRoutes() {
    routing {
        getPokemonRoutes()
    }
}