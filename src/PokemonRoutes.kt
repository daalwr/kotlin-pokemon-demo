package dev.danielwright

import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.json.Json

val client = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer(Json {
            ignoreUnknownKeys = true
        })
    }
}

fun Route.getPokemonRoute() {
    get("/pokemon") {
        val pokemon: Pokemon = client.get("https://pokeapi.co/api/v2/pokemon/1")
        call.respond(pokemon)
    }
}

fun Application.registerPokemonRoutes() {
    routing {
        getPokemonRoute()
    }
}