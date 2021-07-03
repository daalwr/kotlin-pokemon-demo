package dev.danielwright.main

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

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

    val pokemonService: PokemonService by inject()

    get("/hello") {
        call.respondText("Hello, World!")
    }
    get("/pokemon") {
        call.respond(pokemonService.getRandomPokemon())
    }
    post("/pokemon") {
        val pokemonByIdRequest = call.receive<GetPokemonByIdRequest>()
        call.respond(pokemonService.getPokemonById(pokemonByIdRequest.id))
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

fun Application.registerPokemonRoutes() {
    routing {
        getPokemonRoutes()
    }
}