package dev.danielwright

import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.json.Json
import kotlin.random.Random

val client = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer(Json {
            ignoreUnknownKeys = true
        })
    }
}


fun Route.getPokemonRoute() {
    get("/pokemon") {
        val randomNumber: Int = Random.nextInt(1, 151)
        val pokemon = getPokemon(randomNumber)
        call.respond(pokemon)
    }
}

suspend fun getPokemon(number: Int): Pokemon {
    return client.get("https://pokeapi.co/api/v2/pokemon/$number")
}

fun Application.registerPokemonRoutes() {
    routing {
        getPokemonRoute()
    }
}